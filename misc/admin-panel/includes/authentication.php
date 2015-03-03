<?php

require_once "admin-db.php";
require_once "password.php";

function sec_session_start() {
    $session_name = 'sec_session_id';
    $secure = SECURE;
    $httponly = true;
    if (ini_set('session.use_only_cookies', 1) === FALSE) {
        header("Location: ../error.php?err=Session init failed (ini_set)");
        exit();
    }
    $cookieParams = session_get_cookie_params();
    session_set_cookie_params($cookieParams["lifetime"], $cookieParams["path"],
                              $cookieParams["domain"], $secure, $httponly);
    session_name($session_name);
    session_start();
    session_regenerate_id(true);
}

function login($username, $password, $db) {
    if ($stmt = $db->prepare("SELECT id, password FROM admins WHERE username = ? LIMIT 1")) {
        $stmt->bind_param('s', $username);
        $stmt->execute();
        $stmt->store_result();
        
        $stmt->bind_result($user_id, $db_password);
        $stmt->fetch();
        
        if ($stmt->num_rows >= 1) { 
            if (is_brute_force($user_id, $db)) {
                return -3;
            } else {
                if (validate($password, $db_password)) {
                    $user_browser = $_SERVER['HTTP_USER_AGENT'];
                    $user_id = preg_replace("/[^0-9]+/", "", $user_id);
                    $_SESSION['user_id'] = $user_id;
                    $username = preg_replace("/[^a-zA-Z0-9_\-]+/", "", $username);
                    $_SESSION['username'] = $username;
                    $_SESSION['login_string'] = hash('sha512', $db_password . $user_browser);
                    return 1;
                } else {
                    on_login_failed($user_id, $db);
                    return -1;
                }
            }
        } else {
            return -2;
        }
    }
}

function on_login_failed($user_id, $db) {
    $stmt = $db->prepare("INSERT INTO login_attempts(user_id) VALUES (?)");
    $stmt->bind_param('i', $user_id);
    $stmt->execute();
    $stmt->close();
}

function is_brute_force($user_id, $db) {
    $time = time();
    
    $valid_attempts = $time - (2 * 60 * 60);
    
    $check_query = "SELECT time FROM login_attempts WHERE user_id = ? AND timestamp > ?";
 
    if ($stmt = $db->prepare($check_query)) {
        $stmt->bind_param('ii', $user_id, $valid_attempts);
        $stmt->execute();
        $stmt->store_result();
        
        if ($stmt->num_rows > 5) {
            return true;
        } else {
            return false;
        }
    }
}

function login_check($db) {
    if (isset($_SESSION['user_id'], $_SESSION['username'], $_SESSION['login_string'])) {
        $user_id = $_SESSION['user_id'];
        $login_string = $_SESSION['login_string'];
        $username = $_SESSION['username'];
        
        $user_browser = $_SERVER['HTTP_USER_AGENT'];
        
        if ($stmt = $db->prepare("SELECT password FROM admins WHERE id = ? LIMIT 1")) {
            $stmt->bind_param('i', $user_id);
            $stmt->execute();
            $stmt->store_result();
 
            if ($stmt->num_rows >= 1) {
                $stmt->bind_result($password);
                $stmt->fetch();
                $login_hash = hash('sha512', $password . $user_browser);
 
                if ($login_hash == $login_string) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return -2;
            }
        } else {
            return -3;
        }
    } else {
        return -4;
    }
}

function esc_url($url) {
    if ('' == $url) {
        return $url;
    }
    
    $url = preg_replace('|[^a-z0-9-~+_.?#=!&;,/:%@$\|*\'()\\x80-\\xff]|i', '', $url);
    
    $strip = array('%0d', '%0a', '%0D', '%0A');
    $url = (string) $url;
    
    $count = 1;
    while ($count) {
        $url = str_replace($strip, '', $url, $count);
    }
    
    $url = str_replace(';//', '://', $url);
    
    $url = htmlentities($url);
    
    $url = str_replace('&amp;', '&#038;', $url);
    $url = str_replace("'", '&#039;', $url);
    
    if ($url[0] !== '/') {
        return '';
    } else {
        return $url;
    }
}

?>
