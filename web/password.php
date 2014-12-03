<?php
define('ALGORITHM', 'sha256');
define('HASH_LENGTH', 24);
define('SALT_LENGTH', 24);
define('ITERATIONS', 1000);

function hash_password($password)
{
    $salt = base64_encode(mcrypt_create_iv(SALT_LENGTH, MCRYPT_DEV_URANDOM));
    $hash = base64_encode(hash_key(ALGORITHM, $password, $salt, ITERATIONS, HASH_LENGTH));
    return ITERATIONS . '$' .  $salt . '$' . $hash;
}

function validate($password, $stored_password)
{
    $parts = explode("$", $stored_password);
    
    if(count($parts) < 3)
       return false;
    
    $stored_hash = base64_decode($parts[2]);
    
    $hash = hash_key(ALGORITHM, $password, $parts[1], (int)$parts[0], strlen($stored_hash));
    
    return $stored_hash === $hash;
}

function hash_key($algorithm, $key, $salt, $iterations, $hash_length)
{
    $length = strlen(hash($algorithm, '', true));
    $count = ceil($hash_length / $length);

    $output = '';
    
    for($i = 1; $i <= $count; $i++) {
        $last = $salt . pack('N', $i);
        
        $last = $xorsum = hash_hmac($algorithm, $last, $key, true);
        
        for ($j = 1; $j < $iterations; $j++) {
            $xorsum ^= ($last = hash_hmac($algorithm, $last, $key, true));
        }
        
        $output .= $xorsum;
    }
    
    return substr($output, 0, $hash_length);
}
?>
