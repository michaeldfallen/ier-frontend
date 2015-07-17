# Cookies

Frontend was build as stateless service. No session state data is kept on server side.
Client is responsible to keep session data locally and carry them back and forth with 
every request.

## Transaction cookies

Every cookie is encrypted and goes in pair, the payload cookie and [initialisation vector]
(http://en.wikipedia.org/wiki/Initialisation_vector) (IV) cookie. The IV cookie contains
one single numeric value generated for every response using . 

 - ***application*** - session payload cookie, contains only application payload data like 
   InprogressOrdinary or InprogressCrown with user name, address, NINO, DOB; all serialized to 
   JSON and encrypted using IV. When there was no change in application data, the cookie does not 
   change as well.
   
 - ***sessionKey*** - session token cookie. Always generated new with every response. Also encrypted
   with its own IV. It is created on the first step of the transaction and deleted on Exit page
   of after successful application submission in Confirmation step.
   The session cookie data contains: 
    - timestamps to check for timeout 
    - transaction _history_ - time spent on individual steps in form of list of 
   timestamps as a simple anti-bot measure
    - _session ID_   

Why separated application and session cookies? - optimisation

 - When there was no change in application data cookie does not change.
   That saves some CPU because there is no encoding and network traffic as no updated cookie data 
   are sent.
   
 - Session token, usually smaller cookie, is decrypted and deserialized separately as first, 
   session is checked for validity and only when a valid session is detected the main payload
   cookie is decrypted.

See: SessionHandling, ResultHandling, CookieHandling, SessionToken, EncryptionService


## Confirmation cookie

When application is submitted, on the end of Confirmation step, all transaction cookies are thrown 
away, both application data and session, and new cookie is created - the ***confirmation*** cookie. 

Confirmation cookie - encrypted serialized JSON + its initialisation vector in a separate cookie:
***confirmation*** and ***confirmationIV***.

It is created by confirmation step after successful submission and serves only Complete page,
containing refNum, local authority (ERO) data because authority details are currently retrievable 
only from submission response.

See: ConfirmationCookie


## Technical notes

### 4KB size limit

Size limit to all cookies per domain is 4KB! Some browsers are more tolerant, theoretically with
no limits, some are not, like older Internet Explorer and Safari, requiring sum of all cookies 
combined to be under 4KB. [More info](http://webdesign.about.com/od/cookies/f/cookies-per-domain-limit.htm).

Due to this limitation we have to watch out carefully what to put inside.
As a consequence we have to repeat calls to get address for a postcode because response, 
dubbed _possible addresses_, is potentially too big and breaks the size limit.   


### Security

Frontend does not use a real [nonce](http://en.wikipedia.org/wiki/Cryptographic_nonce)
system, old cookies are not invalidated, our cookies are perfectly valid for 20 minutes (ie until 
they are timeouted) so they can be used to [replay attacks](http://en.wikipedia.org/wiki/Replay_attack).
No replay attack scenario was deemed as really exploitable for the Frontend. Only application submission 
(Confirmation step) could be, the protective measures were implemented on backend side (IER-API) where
repeated application is recognized and silently ignored.

The `java.security.SecureRandom` is used to generate Initialisation Vector, see `EncryptionService`.

  
### Assets

Assets are images, JavaScript and CSS files. With default set up cookies would 
be sent with every asset request. As cookies can grow to several KBs, and there can be dozens of 
assets requests per page, that would be highly inefficient. Recommended solution is to use
separate domain for assets, and that is what we use on production environments, on local dev 
environments default _same domain_ setup is used for simplicity. 

On Preview, Staging and Production environements the `asset.path` config setting in `conf/ier-frontend.conf`
is set so assets are served off a different domain. This means cookies are not sent on this asset domain.

### Outside session pages

Feedback form, static text pages like Cookies or Privacy pages. All this pages have session data 
available, if they were called from session step, because they are part of the same domain
but they don't use them and they do not refresh _session token_.

Authority lookup currently does use session data.
 
Complete page, the very last step in the registration process, has it's own payload cookie.
This step is not part of session, both _application_ and _sessionKey_ must have been deleted by 
that time.
