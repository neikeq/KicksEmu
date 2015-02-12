<?php

require_once "admin-db.php";
require_once "authentication.php";

sec_session_start(); // Our custom secure way of starting a PHP session.

if (isset($_POST['username'], $_POST['password'])) {
    $email = $_POST['username'];
    $password = $_POST['password']; // The hashed password.
 
    $aux = login($email, $password, $db);
 
    if ($aux == 1) {
        // Login success 
        header('Location: ../moderation.php');
    } else {
        // Login failed 
        header("Location: ../index.php?error=$aux");
    }
} else {
    // The correct POST variables were not sent to this page. 
    echo 'Invalid Request';
}

?>