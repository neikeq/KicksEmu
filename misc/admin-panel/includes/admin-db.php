<?php

define("SECURE", false);

$db_info = array(
    'host' => 'localhost',
    'user' => 'root',
    'pass' => '',
    'name' => 'kicksap'
);

$db = new mysqli(
    $db_info['host'],
    $db_info['user'],
    $db_info['pass'],
    $db_info['name']
);

if ($db->connect_errno > 0) {
    echo "Database connection error.";
    exit;
}

?>