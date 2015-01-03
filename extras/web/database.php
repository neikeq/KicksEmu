<?php
define('DB_HOST', 'localhost');
define('DB_NAME', 'kicksdb');
define('DB_USER', 'root');
define('DB_PASS', '');

define('MIN_USER_LENGTH', 5);
define('MAX_USER_LENGTH', 15);
define('MIN_PASS_LENGTH', 6);
define('MAX_PASS_LENGTH', 15);

$dsn = 'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME;

$options = array(
    PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES utf8',
); 

$db = new PDO($dsn, DB_USER, DB_PASS, $options);
?>
