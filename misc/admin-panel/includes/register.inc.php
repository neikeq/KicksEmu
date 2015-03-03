<!--
This file (along with ../register.php) must not be included in any final release.
This is a registration form example, and users should not be allowed to join.
-->

<?php

require_once 'admin-db.php';
require_once 'password.php';
 
$error_msg = "";
 
if (isset($_POST['username'], $_POST['password'])) {
    $username = filter_input(INPUT_POST, 'username', FILTER_SANITIZE_STRING);
    $password = filter_input(INPUT_POST, 'password', FILTER_SANITIZE_STRING);
    
    if (strlen($password) != 128) {
        $error_msg .= '<p class="error">Invalid password configuration.</p>';
    }
    
    $stmt = $db->prepare("SELECT id FROM admins WHERE username = ? LIMIT 1");
    
    if ($stmt) {
        $stmt->bind_param('s', $username);
        $stmt->execute();
        $stmt->store_result();
 
        if ($stmt->num_rows >= 1) {
            $error_msg .= '<p class="error">A user with this email address already exists.</p>';
                        $stmt->close();
        }
        
        $stmt->close();
    } else {
        $error_msg .= '<p class="error">Database error Line 39</p>';
    }
    
    $stmt = $db->prepare("SELECT id FROM admins WHERE username = ? LIMIT 1");
 
    if ($stmt) {
        $stmt->bind_param('s', $username);
        $stmt->execute();
        $stmt->store_result();
 
        if ($stmt->num_rows >= 1) {
                $error_msg .= '<p class="error">A user with this username already exists</p>';
                $stmt->close();
        }
        $stmt->close();
    } else {
        $error_msg .= '<p class="error">Database error line 55</p>';
    }
 
    if (empty($error_msg)) {  
        $password = hash_password($password);
        
        if ($insert_stmt = $db->prepare("INSERT INTO admins (username, password) VALUES (?, ?)")) {
            $insert_stmt->bind_param('ss', $username, $password);
            if ($insert_stmt->execute()) {
                header('Location: ../index.php?msg=Register success');
            } else {
                header('Location: ../error.php?err=Registration failure on database INSERT.');
            }
        }
    }
}

?>
