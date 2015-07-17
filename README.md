#IER Frontend

This is the Individual Electoral Registration (aka ERTP) Frontend app. The point of it is to provide a simple Register to Vote form to the public. 

***
Please note that this source code will not run as is, as it has API dependencies which are not open sourced. This code is presented for informational purposes only.
***

###Depedencies

To run this app you will need: 

 - Java 7 (we use openJDK 7)
 - Ruby 1.9.3
 - Sass
 - Bundler

All other depedencies (Play framework, Scala, sbt, etc) will be installed as part of the `sbt` start script.

###Bootstrap project and run the service

 1. `git clone git@github.gds:gds/ier-frontend.git`

 2. In a terminal execute `./sbt` to open the Play console
 
 3. Wait (Downloading the entire internet)
 
 4. In the SBT console execute `compile` to compile the app
 
 5. Create directory `/var/log/ier` with write access rights for the current user, e.g:   

    ```
    _sudo mkdir /var/log/ier_  
    _sudo chown root:users /var/log/ier_  
    _sudo chmod 770 /var/log/ier_  
    ```

 6. In the SBT console execute `run` to start the app
 
 7. Go to [http://localhost:9000/](http://localhost:9000/)  

    \[Note: I got an internal error had to re- `gem install sass` to fix it \]
 
###Running the service

 1. In a terminal execute `./sbt` to open the SBT console

 2. In the SBT console execute `run` to start the app
 
 3. Or just `./sbt run`
 
 4. Go to `http://localhost:9000/`

###Front-end development

The CSS and JS files Play serves are compiled versions of those in the `./assets` folder.

There are [Gulp](http://gulpjs.com/) tasks to build the compiled CSS and JS in the `./scripts/gulp` folder.

The `./scripts/gulp` folder has a guide to setting up Gulp and to the tasks in its README.

###Running the tests

Testing for the ier-frontend project is via the sbt command line as follows...

 1. In the terminal execute `./sbt` to open the SBT console

 2. In the SBT console execute `test` to begin ALL the tests

 3. Or just `./sbt test`

 **NB : Make sure you follow the Bootstrap Step 5 above to make the /var/log/ier/ folder locally wriateable.**

 **NB : To run a specific test rather than all of them  `test-only FULL_PATH_OF_TEST_CLASS`**
