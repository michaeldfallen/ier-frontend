#!/bin/bash

echo "`lsof | grep txt | wc -l` - `date`" >> /tmp/test.txt
