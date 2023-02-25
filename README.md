### Postgres

Enter in cmd : 
```
psql -U postgres
Password for user postgres: //Enter password
psql (14.6)
WARNING: Console code page (437) differs from Windows code page (1252)
8-bit characters might not work correctly. See psql reference
page "Notes for Windows users" for details.
Type "help" for help.

postgres=# CREATE DATABASE blog;
CREATE DATABASE
postgres=# CREATE USER blog_user WITH ENCRYPTED PASSWORD 'blog_password';
CREATE ROLE
postgres=# GRANT ALL PRIVILEGES ON DATABASE blog TO blog_user;
GRANT
postgres=#\q //To quit

C:\Users\suhasini>psql -U blog_user postgres
Password for user blog_user: //Enter password
psql (14.6)
WARNING: Console code page (437) differs from Windows code page (1252)
8-bit characters might not work correctly. See psql reference
page "Notes for Windows users" for details.
Type "help" for help.
```
