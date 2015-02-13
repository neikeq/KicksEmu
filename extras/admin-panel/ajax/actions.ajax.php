<?php

require_once "../includes/database.php";
require_once "user-utils.ajax.php";
require_once "constants.ajax.php";

function ban_user($username, $expire, $reason, $db) {
    $user_id = get_user_id($username, $db);
    
    $stmt = $db->prepare("INSERT INTO bans(user_id, expire, reason) VALUES(?,?,?)");
    $stmt->bind_param("iss", $user_id, $expire, $reason);
    $stmt->execute();
    $result = $stmt->fetch();
    $stmt->close();
    
    return $result;
}

function add_points($character, $value, $db) {
    $char_id = get_character_id($character, $db);
    
    $stmt = $db->prepare("UPDATE characters SET points=points+? WHERE id=?");
    $stmt->bind_param("ii", $value, $char_id);
    $stmt->execute();
    $result = $stmt->fetch();
    $stmt->close();
    
    return $result;
}

function add_kash($username, $value, $db) {
    $user_id = get_user_id($username, $db);
    
    $stmt = $db->prepare("UPDATE users SET kash=kash+? WHERE id=?");
    $stmt->bind_param("ii", $value, $user_id);
    $stmt->execute();
    $result = $stmt->fetch();
    $stmt->close();
    
    return $result;
}

function add_experience($character, $value, $db) {
    $char_id = get_character_id($character, $db);
    
    $stmt = $db->prepare("UPDATE characters SET experience=experience+? WHERE id=?");
    $stmt->bind_param("ii", $value, $char_id);
    $stmt->execute();
    $result = $stmt->fetch();
    $stmt->close();
    
    check_character_experience($char_id, $db);
    
    return $result;
}

function add_item($character, $item_id, $expiration, $bonus_one,
                  $bonus_two, $usages, $timestamp, $db) {
    $char_id = get_character_id($character, $db);
    
    $result = 0;
    
    if ($char_id > 0 && !already_purchased($char_id, $item_id, "items", "item_id", $db)) {
        $inventory_id = next_inventory_id($char_id, "items", $db);
        
        $stmt = $db->prepare("INSERT INTO items VALUES(?,?,?,?,?,?,?,?,?,?)");
        $stmt->bind_param("iiiiiiisii", $char_id, $inventory_id, $item_id, $expiration,
                          $bonus_one, $bonus_two, $usages, $timestamp, $s = 0, $v = 1);
        $stmt->execute();
        $result = $stmt->fetch();
        $stmt->close();
    }
    
    return $result;
}

function add_skill($character, $skill_id, $expiration, $timestamp, $db) {
    $char_id = get_character_id($character, $db);
    
    $result = 0;
    
    if ($char_id > 0 && !already_purchased($char_id, $skill_id, "skills", "skill_id", $db)) {
        $inventory_id = next_inventory_id($char_id, "skills", $db);
        
        $stmt = $db->prepare("INSERT INTO skills VALUES(?,?,?,?,?,?,?)");
        $stmt->bind_param("iiiiisi", $char_id, $inventory_id, $skill_id,
                          $expiration, $s = 0, $timestamp, $v = 1);
        $stmt->execute();
        $result = $stmt->fetch();
        $stmt->close();
    }
    
    return $result;
}

function add_cere($character, $cere_id, $expiration, $timestamp, $db) {
    $char_id = get_character_id($character, $db);
    
    $result = 0;
    
    if ($char_id > 0 && !already_purchased($char_id, $cere_id, "ceres", "cere_id", $db)) {
        $inventory_id = next_inventory_id($char_id, "ceres", $db);
        
        $stmt = $db->prepare("INSERT INTO ceres VALUES(?,?,?,?,?,?,?)");
        $stmt->bind_param("iiiiisi", $char_id, $inventory_id, $cere_id,
                          $expiration, $s = 0, $timestamp, $v = 1);
        $stmt->execute();
        $result = $stmt->fetch();
        $stmt->close();
    }
    
    return $result;
}

function add_learn($character, $learn_id, $db) {
    $char_id = get_character_id($character, $db);
    
    $result = 0;
    
    if ($char_id > 0 && !already_purchased($char_id, $learn_id, "learns", "learn_id", $db)) {
        $inventory_id = next_inventory_id($char_id, "learns", $db);
        
        $stmt = $db->prepare("INSERT INTO learns VALUES(?,?,?,?)");
        $stmt->bind_param("iiii", $char_id, $inventory_id, $learn_id, $v = 1);
        $stmt->execute();
        $result = $stmt->fetch();
        $stmt->close();
    }
    
    return $result;
}

function reset_stats($character, $db) {
    $char_id = get_character_id($character, $db);
    return reset_stats_by_id($char_id, $db);
}

function reset_stats_by_id($char_id, $db) {
    $position = get_character_position($char_id, $db);
    $level = get_character_level($char_id, $db);
    $branch_position = floor($position / 10) * 10;
    $creation_stats = Constants::$creation_stats[$branch_position];
    
    set_character_stats_points($char_id, 10, $db);
    
    for ($i = 0; $i < 17; $i++) {
        set_character_stats_by_index($char_id, $creation_stats[$i], $i, $db);
    }
    
    if ($level > 18) {
        on_character_level_up($char_id, 18, 17, $branch_position, $db);
        apply_upgrade_stats($char_id, $position, $db);
        on_character_level_up($char_id, $level, $level - 18, $position, $db);
    } else {
        on_character_level_up($char_id, $level, $level - 1, $position, $db);
    }
}

function reset_stats_global($reason, $db) {
    $result = 1;

    $stmt = $db->prepare("SELECT id FROM characters");
    $stmt->bind_result($char_id);
    $stmt->execute();

    $chars = array();
    
    while ($stmt->fetch()) {
        array_push($chars, $char_id);
    }
    
    $stmt->close();
    
    for ($i = 0; $i < count($chars); $i++) {
        $result |= reset_stats_by_id($chars[$i], $db);
    }
    
    return $result;
}

function apply_upgrade_stats($char_id, $position, $db) {
    $remain_stats = 0;

    for ($i = 0; $i < 17; $i++) {
        $remain_stats += sum_character_stats_by_index($char_id, Constants::$upgrade_stats[$position][$i],
                                                      $i, $db);
    }

    sum_character_stats_points($remain_stats, $char_id, $db);
}

function change_position($character, $position, $db) {
    $char_id = get_character_id($character, $db);
    
    $stmt = $db->prepare("UPDATE characters SET position=? WHERE id=?");
    $stmt->bind_param("ii", Constants::$positions[strtoupper($position)], $char_id);
    $stmt->execute();
    $result = $stmt->fetch();
    $stmt->close();
    
    $result |= reset_stats_by_id($char_id, $db);
    $result |= remove_all_character_skills($char_id, $db);
    
    return $result;
}

function change_name($character, $new_name, $db) {
    $char_id = get_character_id($character, $db);
    
    $result = false;
    
    if (!character_exists($new_name, $db)) {
        $stmt = $db->prepare("UPDATE characters SET name=? WHERE id=?");
        $stmt->bind_param("si", $new_name, $char_id);
        $stmt->execute();
        $result = $stmt->fetch();
        $stmt->close();
    }
    
    return $result;
}

if (isset($_POST["action_id"])) {
    switch ($_POST["action_id"]) {
        case "ban_user":
            ban_user($_POST["param1"], $_POST["param2"], $_POST["param3"], $db);
            break;
        case "add_points":
            add_points($_POST["param1"], $_POST["param2"], $db);
            break;
        case "add_kash":
            add_kash($_POST["param1"], $_POST["param2"], $db);
            break;
        case "add_exp":
            add_experience($_POST["param1"], $_POST["param2"], $db);
            break;
        case "add_item":
            add_item($_POST["param1"], $_POST["param2"], $_POST["param3"], $_POST["param4"],
                     $_POST["param5"], $_POST["param6"], $_POST["param7"], $db);
            break;
        case "add_skill":
            add_skill($_POST["param1"], $_POST["param2"], $_POST["param3"],
                      $_POST["param4"], $db);
            break;
        case "add_cere":
            add_cere($_POST["param1"], $_POST["param2"], $_POST["param3"],
                     $_POST["param4"], $db);
            break;
        case "add_learn":
            add_learn($_POST["param1"], $_POST["param2"], $db);
            break;
        case "reset_stats":
            reset_stats($_POST["param1"], $db);
            break;
        case "reset_stats_g":
            reset_stats_global($_POST["param1"], $db);
            break;
        case "change_pos":
            change_position($_POST["param1"], $_POST["param2"], $db);
            break;
        case "change_name":
            change_name($_POST["param1"], $_POST["param2"], $db);
            break;
        default:
    }
}

?>