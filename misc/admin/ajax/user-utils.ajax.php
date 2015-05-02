<?php

require_once "../includes/database.php";
require_once "constants.ajax.php";

function refValues($arr){
    if (strnatcmp(phpversion(),'5.3') >= 0)
    {
        $refs = array();
        foreach($arr as $key => $value)
            $refs[$key] = &$arr[$key];
        return $refs;
    }
    return $arr;
}

function character_exists($name, $db) {
    $stmt = $db->prepare("SELECT 1 FROM characters WHERE name=?");
    $stmt->bind_param("s", $name);
    $stmt->execute();
    $result = $stmt->fetch();
    $stmt->close();

    return $result;
}

function get_user_id($username, $db) {
    $stmt = $db->prepare("SELECT id FROM users WHERE username=?");
    $stmt->bind_param("s", $username);
    $stmt->bind_result($user_id);
    $stmt->execute();

    if (!$stmt->fetch()) {
        $user_id = -1;
    }

    $stmt->close();

    return $user_id;
}

function get_character_id($name, $db) {
    $stmt = $db->prepare("SELECT id FROM characters WHERE name=?");
    $stmt->bind_param("s", $name);
    $stmt->bind_result($char_id);
    $stmt->execute();

    if (!$stmt->fetch()) {
        $char_id = -1;
    }

    $stmt->close();

    return $char_id;
}

function get_character_level($char_id, $db) {
    $stmt = $db->prepare("SELECT level FROM characters WHERE id=?");
    $stmt->bind_param("i", $char_id);
    $stmt->bind_result($level);
    $stmt->execute();

    if (!$stmt->fetch()) {
        $level = -1;
    }

    $stmt->close();

    return $level;
}

function get_character_experience($char_id, $db) {
    $stmt = $db->prepare("SELECT experience FROM characters WHERE id=?");
    $stmt->bind_param("i", $char_id);
    $stmt->bind_result($experience);
    $stmt->execute();

    if (!$stmt->fetch()) {
        $experience = -1;
    }

    $stmt->close();

    return $experience;
}

function get_character_position($char_id, $db) {
    $stmt = $db->prepare("SELECT position FROM characters WHERE id=?");
    $stmt->bind_param("i", $char_id);
    $stmt->bind_result($position);
    $stmt->execute();

    if (!$stmt->fetch()) {
        $position = -1;
    }

    $stmt->close();

    return $position;
}

function get_character_stats_by_type($char_id, $stats_type, $db) {
    $stmt = $db->prepare("SELECT stats_" . $stats_type . " FROM characters WHERE id=?");
    $stmt->bind_param("i", $char_id);
    $stmt->bind_result($stats_by_type);
    $stmt->execute();

    if (!$stmt->fetch()) {
        $stats_by_type = -1;
    }

    $stmt->close();

    return $stats_by_type;
}

function set_character_level($char_id, $level, $db) {
    $stmt = $db->prepare("UPDATE characters SET level=? WHERE id=?");
    $stmt->bind_param("ii", $level, $char_id);
    $stmt->execute();
    $result = $stmt->fetch();
    $stmt->close();

    return $result;
}

function set_character_stats($char_id, $stats_points, $stats, $db) {
    $stmt = $db->prepare("UPDATE characters SET " .
        "stats_running = ?, stats_endurance = ?, stats_agility = ?, " .
        "stats_ball_control = ?, stats_dribbling = ?, stats_stealing = ?, " .
        "stats_tackling = ?, stats_heading = ?, stats_short_shots = ?, " .
        "stats_long_shots = ?, stats_crossing = ?, stats_short_passes = ?, " .
        "stats_long_passes = ?, stats_marking = ?, stats_goalkeeping = ?, " .
        "stats_punching = ?, stats_defense = ?, stats_points = ? " .
        "WHERE id = ? LIMIT 1;");
    call_user_func_array(array($stmt, "bind_param"), refValues(array_merge(array('iiiiiiiiiiiiiiiiiii'), $stats, array($stats_points, $char_id))));
    //$stmt->bind_param("ii", $stats_points, $char_id);
    $stmt->execute();
    $result = $stmt->fetch();
    $stmt->close();

    return $result;
}

function sum_character_stats_points($char_id, $stats_points, $db) {
    if ($stats_points == 0) return false;

    $stmt = $db->prepare("UPDATE characters SET stats_points=stats_points+? WHERE id=?");
    $stmt->bind_param("ii", $stats_points, $char_id);
    $stmt->execute();
    $result = $stmt->fetch();
    $stmt->close();

    return $result;
}

function sum_character_stats_by_index($char_id, $value, $index, $db) {
    if ($value == 0) return 0;

    $stats_type = get_stats_type_by_index($index);
    $current_stats = get_character_stats_by_type($char_id, $stats_type, $db);
    $value_final = stats_up_to_hundred($current_stats, $value, $db);

    $stmt = $db->prepare("UPDATE characters SET stats_" . $stats_type .
                         "=stats_" . $stats_type . "+? WHERE id=?");
    $stmt->bind_param("ii", $value_final, $char_id);
    $stmt->execute();
    $stmt->fetch();
    $stmt->close();

    return $value - $value_final;
}

function remove_all_character_skills($char_id, $db) {
    $stmt = $db->prepare("DELETE FROM skills WHERE player_id=?");
    $stmt->bind_param("i", $char_id);
    $stmt->execute();
    $result = $stmt->fetch();
    $stmt->close();

    return $result;
}

function next_inventory_id($char_id, $table, $db) {
    $result = 1;

    $stmt = $db->prepare("SELECT inventory_id FROM " . $table . " WHERE player_id=?");
    $stmt->bind_param("i", $char_id);
    $stmt->bind_result($inventory_id);
    $stmt->execute();

    $ids = array();

    while ($stmt->fetch()) {
        array_push($ids, $inventory_id);
    }

    for ($i = 0; $i < count($ids) + 1; $i++) {
        if (!in_array($i, $ids)) {
            $result = $i;
            break;
        }
    }

    $stmt->close();

    return $result;
}

function already_purchased($char_id, $id, $table, $cel, $db) {
    $stmt = $db->prepare("SELECT inventory_id FROM " . $table .
                         " WHERE player_id=? AND " . $cel . "=?");
    $stmt->bind_param("ii", $char_id, $id);
    $stmt->bind_result($inventory_id);
    $stmt->execute();
    $result = $stmt->fetch();
    $stmt->close();

    return $result;
}

function get_level_for_experience($cur_exp, $cur_level) {
    $new_level = null;

    foreach (Constants::levels as $level => $experience) {
        if ($experience <= $cur_exp && $level > $cur_level) {
            $new_level = $level;
        }
    }

    return $new_level;
}

function check_character_experience($char_id, $db) {
    $levels = 0;
    $level = get_character_level($char_id, $db);
    $experience = get_character_experience($char_id, $db);

    $new_level = get_level_for_experience($experience, $level);

    if (!is_null($new_level)) {
        $levels += $new_level - $level;
        $level = $new_level;
    }

    if ($levels > 0) {
        set_character_level($char_id, $level, $db);
        on_character_level_up($char_id, $level, $levels,
                              get_character_position($char_id, $db), $db);
    }

    return $levels;
}

function on_character_level_up($char_id, $level, $levels, $position, $db) {
    $level_from = $level - $levels;
    $stats_points = 0;
    for ($i = $level_from; $i < $level; $i++) {
        $index = $i + 1;
        $stats_to_add = array_key_exists($index, Constants::stats_for_level) ?
                        Constants::stats_for_level[$index] : 1;
        $stats_points += $stats_to_add;
    }
    $auto_stats = Constants::auto_stats[$position];
    for ($i = 0; $i < count($auto_stats); $i++) {
        $stats_points += sum_character_stats_by_index($char_id, $auto_stats[$i] * $levels,
                                                      $i, $db);
    }
    sum_character_stats_points($char_id, $stats_points, $db);
}

function on_level_up_optimized($level, $levels, $position, &$stats) {
    $level_from = $level - $levels;

    $stats_points = 0;

    for ($i = $level_from; $i < $level; $i++) {
        $index = $i + 1;
        $stats_to_add = array_key_exists($index, Constants::stats_for_level) ?
                        Constants::stats_for_level[$index] : 1;
        $stats_points += $stats_to_add;
    }

    $auto_stats = Constants::auto_stats[$position];
    sum_stats($auto_stats, $levels, $stats, $stats_points);

    return $stats_points;
}

function sum_stats($add, $factor, &$stats, &$stats_points) {
    for ($i = 0; $i < 17; $i++) {
        $stats[$i] += sum_stats_up_to_hundred($add[$i] * $factor, $stats[$i], $stats_points);
    }
}

function sum_stats_up_to_hundred($value, $current, &$stats_points) {
    $add = stats_up_to_hundred($current, $value);
    $stats_points += $value - $add;

    return $add;
}

function stats_up_to_hundred($current_value, $value) {
    if ($value < 0) return $value;

    $i = 0;

    while ($i < $value) {
        if ($current_value < 100) {
            $current_value++;
        } else {
            break;
        }
        $i++;
    }

    return $i;
}

function get_stats_type_by_index($index) {
    switch ($index) {
        case 0:
            return "running";
        case 1:
            return "endurance";
        case 2:
            return "agility";
        case 3:
            return "ball_control";
        case 4:
            return "dribbling";
        case 5:
            return "stealing";
        case 6:
            return "tackling";
        case 7:
            return "heading";
        case 8:
            return "short_shots";
        case 9:
            return "long_shots";
        case 10:
            return "crossing";
        case 11:
            return "short_passes";
        case 12:
            return "long_passes";
        case 13:
            return "marking";
        case 14:
            return "goalkeeping";
        case 15:
            return "punching";
        case 16:
            return "defense";
    }
}

?>
