#!/usr/bin/python

from multiprocessing import Process, Manager
import multiprocessing
import os
import urllib
import urllib2
import random
import time
import json
import string
import random
import math

def randomString(size=6, chars=string.ascii_uppercase + string.digits):
	return ''.join(random.choice(chars) for x in range(size))

def registration(snac):
	return {
		"eroRecordId": randomString(10),
    	"applicant.firstName": randomString(10),
    	"applicant.lastName": randomString(10),
    	"address.current.lineOne": randomString(10),
    	"address.current.lineTwo": randomString(10),
    	"address.current.lineThree": randomString(10),
    	"address.current.lineFour": randomString(10),
    	"address.current.lineFive": randomString(10),
    	"address.current.postcode": "A11AA",
    	"localAuthority.current.snacCode" : snac
		}

def registrations(page_size, snac):
	lotsOfRegistrations = []
	for i in range(int(page_size)):
		lotsOfRegistrations.append(registration(snac))
	return {"registrations" : lotsOfRegistrations}

def get(url , token):
	get_request, opener = request("GET", url, None, token, False)
	try:
	    connection = opener.open(get_request)
	except urllib2.HTTPError,e:
	    connection = e
	
	if connection.code == 200:
		data = connection.read()
		return data
	else:
		data = connection.read()

def request(request_method, url, data, token, isJson):
	method = request_method
	handler = urllib2.HTTPHandler()
	opener = urllib2.build_opener(handler)
	request = urllib2.Request(url, data=data )
	if(isJson):
		request.add_header("Content-Type",'application/json')
	request.add_header("Authorization",'Bearer ' + token)
	request.get_method = lambda: method
	return request, opener
	
def post(url, token, data):
	post_request, opener = request("POST", url, json.dumps(data), token, True)
	
	try:
	    connection = opener.open(post_request)
	except urllib2.HTTPError,e:
	    connection = e
	
	if connection.code == 200:
		data = connection.read()
	else:
		data = connection.read()

def read_config():
	 return json.load(open('exceriser_config.json')) 

def total_errors(quantity, percentage_error):
	return int(math.ceil(float(quantity) * int(percentage_error) / 100))	

def do_json_errors(url, token, snac, quantity, page_size, percentage_to_be_errors, frequency):
	json_errors = total_errors(quantity, percentage_to_be_errors)
	for i in range(json_errors):
		post(url, token, {"invalid" : "json"})
		limiter(frequency)
		
def do_validation_errors(url, token, snac, quantity, page_size, percentage_to_be_errors, frequency):
	validation_errors = total_errors(quantity, percentage_to_be_errors)
	
	badRegistration = registration(snac)	
	badRegistration['address.current.postcode'] = "invalid"
	badRegistration['address.current.lineOne'] = randomString(300)
	for i in range(validation_errors):
		badRegistrations = []
		badRegistrations.append(badRegistration)
		post(url, token, {"registrations" : badRegistrations})
		limiter(frequency)

def do_excerise(url, token, snac, quantity, page_size, frequency):
	number_of_requests = int(math.ceil(float(quantity) / int(page_size)))
	
	for i in range(number_of_requests):
		post(url, token, registrations(page_size, snac))
		limiter(frequency)			
	
def mark_retrieved(url, token, data):
	post(url, None)	
	
def do_fetch(url, token, frequency, fetches, page_size, consume_queue):
	
	ertp_ids = []
	
	for i in range(fetches):
		response = get(url, token)
		response_as_json = json.loads(response)
		registrations = response_as_json['registrations']
		for reg in registrations:
			ertp_ids.append(reg['id'])
			
		if(consume_queue):
			post(url + "/received", token, {"receivedIds" : ertp_ids})
			ertp_ids = []
			
		limiter(frequency)

def limiter(time_to_pause):
	if(time_to_pause != -1):
		time.sleep(time_to_pause)
	
if __name__ == '__main__':
	config = read_config()
	baseUrl = config['url']
			
	for ero in config['eros']:
		if(ero['upload']['include']):
			p1 = Process(target=do_excerise, args=(baseUrl, ero['token'], ero['snac'], ero['upload']['uploadQuantity'], ero['upload']['pageSize'], ero['upload']['frequency']))
			p1.start()	
		if(ero['validationErrors']['include']):
			validiation_process = Process(target=do_validation_errors, args=(baseUrl, ero['token'], ero['snac'], ero['upload']['uploadQuantity'], ero['upload']['pageSize'], ero['validationErrors']['percentage'], ero['validationErrors']['frequency']))
			validiation_process.start()
		if(ero['jsonErrors']['include']):
			json_process = Process(target=do_json_errors, args=(baseUrl, ero['token'], ero['snac'], ero['upload']['uploadQuantity'], ero['upload']['pageSize'], ero['jsonErrors']['percentage'], ero['jsonErrors']['frequency']))
			json_process.start()
		if(ero['queueFetch']['include']):
			fetch_process = Process(target=do_fetch, args=(baseUrl, ero['token'], ero['queueFetch']['frequency'], ero['queueFetch']['fetches'], ero['queueFetch']['pageSize'], ero['queueFetch']['consumeQueue']))
			fetch_process.start()
		
	multiprocessing.active_children()