$(document).ready(function() {
    $(".loader").css("display", "none");
    
    $(document).on('click', '.action_button', function() {
        $id = $(this).attr('id');
        
        var dataParams = "";
        
        switch ($id) {
            case "ban_user":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#bu_user").val(),
                    'param2' : $("#bu_expire").val(),
                    'param3' : $("#bu_reason").val()
                }
                break;            
            case "add_points":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#ap_character").val(),
                    'param2' : $("#ap_value").val()
                }
                break;
            case "add_kash":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#ak_user").val(),
                    'param2' : $("#ak_value").val()
                }
                break;
            case "add_exp":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#ae_character").val(),
                    'param2' : $("#ae_value").val()
                }
                break;
            case "add_item":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#ai_character").val(),
                    'param2' : $("#ai_id").val(),
                    'param3' : $("#ai_expiration").val(),
                    'param4' : $("#ai_bonusone").val(),
                    'param5' : $("#ai_bonustwo").val(),
                    'param6' : $("#ai_usages").val(),
                    'param7' : $("#ai_expire").val()
                }
                break;
            case "add_skill":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#as_character").val(),
                    'param2' : $("#as_id").val(),
                    'param3' : $("#as_expiration").val(),
                    'param4' : $("#as_expire").val()
                }
                break;
            case "add_cere":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#ac_character").val(),
                    'param2' : $("#ac_id").val(),
                    'param3' : $("#ac_expiration").val(),
                    'param4' : $("#ac_expire").val()
                }
                break;
            case "add_learn":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#at_character").val(),
                    'param2' : $("#at_id").val()
                }
                break;
            case "reset_stats":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#sr_character").val()
                }
                break;
            case "reset_stats_g":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#gsr_reason").val()
                }
                break;
            case "change_pos":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#cp_character").val(),
                    'param2' : $("#cp_position").val()
                }
                break;
            case "change_name":
                dataParams = {
                    'action_id' : $id,
                    'param1' : $("#cn_character").val(),
                    'param2' : $("#cn_name").val()
                }
                break;
            default:
        }
        
        $.ajax({
            url: "ajax/actions.ajax.php" ,
            type: "POST",
            data: dataParams,
            'beforeSend' : function() {
                $(".loader").css("display", "block");
                $("html").not(".loader").addClass("dim");
            },
            success : function(result) {
                $(".loader").css("display", "none");
                $("html").not(".loader").removeClass("dim");
            },
            error : function(textStatus, errorThrows) {
                $(".loader").css("display", "none");
                $("html").not(".loader").removeClass("dim");
                console.log(textStatus + errorThrows);
            }
         });
    });
});