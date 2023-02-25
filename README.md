### Postgres

Enter in command line: 
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

### Mysql

Enter in command line:
```
C:\Users\suhasini>mysql -u root -p
Enter password: ******** 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 19
Server version: 8.0.32 MySQL Community Server - GPL

Copyright (c) 2000, 2023, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> CREATE DATABASE blog;
Query OK, 1 row affected (0.01 sec)

mysql> CREATE USER 'blog_user'@'localhost' IDENTIFIED BY 'blog_password';
Query OK, 0 rows affected (0.01 sec)

mysql> GRANT ALL PRIVILEGES ON blog.* TO 'blog_user'@'localhost';
Query OK, 0 rows affected (0.01 sec)

mysql> exit;
Bye

C:\Users\suhasini>mysql blog -u blog_user -p
Enter password: *************
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 20
Server version: 8.0.32 MySQL Community Server - GPL

Copyright (c) 2000, 2023, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql>
```