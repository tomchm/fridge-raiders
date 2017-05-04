var assets = null;
var OFFSETX = 360;
var OFFSETY = 480;
var current;
var drawplayer = false;
var playeri;
var playerImage = new Image();
var background = "#111111";

function redraw(){

    reassign();

    var c = document.getElementById("myCanvas");
    var ctx = c.getContext("2d");


    //ctx.clearRect(0, 0, c.width, c.height);

    var image = $("#draw_image");
    var src = image.attr("src");
    var width = image.width();
    var height = image.height();
    var scalex = $("#scalex").val();
    var scaley = $("#scaley").val();
    var originx = $("#originx").val();
    var originy = $("#originy").val();
    var type = $("#type").val();

    var imageObj = new Image();

    imageObj.onload = function() {

        ctx.fillStyle=background;
        ctx.fillRect(0,0, c.width, c.height);


        if(type == "image"){
            ctx.drawImage(imageObj, OFFSETX - originx*scalex, OFFSETY + originy*scaley - height*scaley, width*scalex, height*scaley);
        }
        else if(type == "filmstrip"){
            var frames = $("#frames").val();
            var curFrame = $("#frame_range").val();

            ctx.drawImage(imageObj,curFrame*width/frames,0,width/frames,height,OFFSETX - originx*scalex, OFFSETY + originy*scaley - height*scaley, width*scalex/frames, height*scaley);
        }

        if(drawplayer){
            var player = assets.data[playeri];
            ctx.globalAlpha = 0.5;
            ctx.drawImage(playerImage,0,0,playerImage.width/player.frames,playerImage.height,OFFSETX - player.origin_x*player.scale_x, OFFSETY + player.origin_y*player.scale_y - playerImage.height*player.scale_y, playerImage.width*player.scale_x/player.frames, playerImage.height*player.scale_y);
            ctx.globalAlpha = 1;
        }



        ctx.beginPath();
        ctx.moveTo(OFFSETX-64,OFFSETY);
        ctx.lineTo(OFFSETX+64, OFFSETY);
        ctx.strokeStyle = '#00ffff';
        ctx.stroke();
        ctx.closePath();

        ctx.beginPath();
        ctx.moveTo(OFFSETX,OFFSETY-64);
        ctx.lineTo(OFFSETX, OFFSETY+64);
        ctx.strokeStyle = '#00ffff';
        ctx.stroke();
        ctx.closePath();

        if ($('input:radio[name=choose_shape]:checked').val() == "circle"){
            var radius = $("#radius").val();

            ctx.beginPath();
            ctx.arc(OFFSETX, OFFSETY, radius*32, 0, 2 * Math.PI, false);
            ctx.strokeStyle = '#ffff00';
            ctx.stroke();
            ctx.closePath();
        }
        else if ($('input:radio[name=choose_shape]:checked').val() == "rectangle"){
            var width2 = $("#width").val();
            var height2 = $("#height").val();
            var angle = $("#angle").val();

            ctx.save();

            ctx.translate(OFFSETX, OFFSETY);
            ctx.rotate(-angle*Math.PI/180);
            //ctx.translate(-OFFSETX, -OFFSETY);

            ctx.beginPath();
            ctx.strokeStyle = "#ffff00";
            ctx.rect(-width2*16,-height2*16,width2*32,height2*32);
            ctx.stroke();
            ctx.closePath();

            ctx.restore();

        }


    };
    imageObj.src = src;

}

function reassign() {
    assets.data[current].tag = $("#tag").val();
    assets.data[current].type = $("#type").val();
    assets.data[current].filename = $("#filename").val();

    if(assets.data[current].type == "image" || assets.data[current].type == "filmstrip"){
        assets.data[current].scale_x = $("#scalex").val();
        assets.data[current].scale_y = $("#scaley").val();
        assets.data[current].origin_x = $("#originx").val();
        assets.data[current].origin_y = $("#originy").val();
        assets.data[current].shape = $('input:radio[name=choose_shape]:checked').val();

        delete assets.data[current].radius;
        delete assets.data[current].height;
        delete assets.data[current].width;
        delete assets.data[current].angle;

        if (assets.data[current].shape == "circle"){
            assets.data[current].radius = $("#radius").val();
        }
        else if (assets.data[current].shape == "rectangle"){
            assets.data[current].width = $("#width").val();
            assets.data[current].height = $("#height").val();
            assets.data[current].angle = $("#angle").val();
        }
    }
    if(assets.data[current].type == "filmstrip"){
        assets.data[current].frames = $("#frames").val();
        assets.data[current].speed = $("#speed").val();
    }




    $("#json_input").val(JSON.stringify(assets, null, 2));

    localStorage.jsonFile = $("#json_input").val();
}




$( "#scalex_range").mousemove(function() {
    var val = $("#scalex_range").val();
    $("#scalex").val(val);
    redraw();
});

$( "#scalex").change(function() {
    var val = $("#scalex").val();
    $("#scalex_range").val(val);
    redraw();
});

$( "#scaley_range").mousemove(function() {
    var val = $("#scaley_range").val();
    $("#scaley").val(val);
    redraw();
});

$( "#scaley").change(function() {
    var val = $("#scaley").val();
    $("#scaley_range").val(val);
    redraw();
});

$( "#originx_range").mousemove(function() {
    var val = $("#originx_range").val();
    $("#originx").val(val);
    redraw();
});

$( "#originx").change(function() {
    var val = $("#originx").val();
    $("#originx_range").val(val);
    redraw();
});

$( "#originy_range").mousemove(function() {
    var val = $("#originy_range").val();
    $("#originy").val(val);
    redraw();
});

$( "#originy").change(function() {
    var val = $("#originy").val();
    $("#originy_range").val(val);
    redraw();
});

$( "#radius_range").mousemove(function() {
    var val = $("#radius_range").val();
    $("#radius").val(val);
    redraw();
});

$( "#radius").change(function() {
    var val = $("#radius").val();
    $("#radius_range").val(val);
    redraw();
});

$( "#width_range").mousemove(function() {
    var val = $("#width_range").val();
    $("#width").val(val);
    redraw();
});

$( "#width").change(function() {
    var val = $("#width").val();
    $("#width_range").val(val);
    redraw();
});

$( "#height_range").mousemove(function() {
    var val = $("#height_range").val();
    $("#height").val(val);
    redraw();
});

$( "#height").change(function() {
    var val = $("#height").val();
    $("#height_range").val(val);
    redraw();
});

$( "#angle_range").mousemove(function() {
    var val = $("#angle_range").val();
    $("#angle").val(val);
    redraw();
});

$( "#angle").change(function() {
    var val = $("#angle").val();
    $("#angle_range").val(val);
    redraw();
});

$( "#frames").change(function() {
    redraw();
});

$( "#speed").change(function() {
    redraw();
});

$( "#frame_range").mousemove(function() {
    redraw();
});

$( "#tag").change(function() {
    redraw();
});

$( "#filename").change(function() {
    var filename = $(this).val();
    $("#draw_image").attr("src", "../core/assets/"+filename);
    redraw();
});

$("#show_player").click(function(){
    drawplayer = true;
    $(this).hide();
    $("#hide_player").show();
    redraw();
});

$("#hide_player").click(function(){
    drawplayer = false;
    $(this).hide();
    $("#show_player").show();
    redraw();
});

$("#switch_background").click(function(){
    if(background == "#111111"){
        background = "#EEEEEE";
    }
    else if(background == "#EEEEEE"){
        background = "#ff00ff";
    }
    else {
        background = "#111111";
    }
    redraw();
});

$("#add_asset").click(function(){
    var type = $("#choose_type").val();
    var tag = $("#choose_tag").val();
    if(tag != ""){
        assets.data.push({"type":type, "tag": tag});
        $("#choose_tag").val("");
        $("#choose_type").val("image");
    }
    redraw();
});

$('input:radio[name=choose_shape]').change(function(){
    $(".circle-container").hide();
    $(".rectangle-container").hide();

    if ($('input:radio[name=choose_shape]:checked').val() == "circle") {
        $(".circle-container").show();
    }
    else if ($('input:radio[name=choose_shape]:checked').val() == "rectangle") {
        $(".rectangle-container").show();
    }
    redraw();


});


function loadJSON(){
    assets = JSON.parse($("#json_input").val());

    $("#choose_asset").empty();

    var data = assets.data;
    for(var i = 0; i < data.length; i++){
        if(data[i].type == "image" || data[i].type == "filmstrip" || data[i].type == "sound"){
            var tag = data[i].tag;
            $("#choose_asset").append("<option value='"+tag+"'>"+tag+"</option>");
        }
        if(data[i].tag == "player_down"){
            playeri = i;
        }
    }
}

$("#json_load").click(loadJSON);

$("#choose_asset").change(function(){
   var tag = $("#choose_asset").val();
    if(tag != ""){
        var data = assets.data;
        var datatag = "";
        var i = 0;
        while(tag != datatag){
            datatag = data[i].tag;
            i++;
        }
        i--;

        $(".image-container").hide();
        $(".filmstrip-container").hide();

        $("#tag").val(datatag);
        $("#type").val(data[i].type);
        $("#filename").val(data[i].filename);

        if(data[i].type == "image" || data[i].type == "filmstrip"){
            $(".image-container").show();

            var image = $("#draw_image");
            image.attr("src", "../core/assets/"+data[i].filename);

            $("#scalex").val(data[i].scale_x);
            $("#scaley").val(data[i].scale_y);
            $("#scalex_range").val(data[i].scale_x);
            $("#scaley_range").val(data[i].scale_y);
            $("#originx").val(data[i].origin_x);
            $("#originy").val(data[i].origin_y);
            $("#originx_range").val(data[i].origin_x);
            $("#originy_range").val(data[i].origin_y);

            $('input:radio[name=choose_shape]')[0].checked = false;
            $('input:radio[name=choose_shape]')[1].checked = false;
            $('input:radio[name=choose_shape]')[2].checked = false;

            $(".circle-container").hide();
            $(".rectangle-container").hide();

            if(data[i].shape == "circle"){
                $('input:radio[name=choose_shape]')[0].checked = true;

                $(".circle-container").show();
                $("#radius").val(data[i].radius);
                $("#radius_range").val(data[i].radius);

            }
            else if(data[i].shape == "rectangle"){
                $('input:radio[name=choose_shape]')[1].checked = true;

                $(".rectangle-container").show();
                $("#width").val(data[i].width);
                $("#height").val(data[i].height);
                $("#angle").val(data[i].angle);
                $("#width_range").val(data[i].width);
                $("#height_range").val(data[i].height);
                $("#angle_range").val(data[i].angle);
            }
            else{
                $('input:radio[name=choose_shape]')[2].checked = true;
            }
        }
        if(data[i].type == "filmstrip"){
            $(".filmstrip-container").show();
            $("#frames").val(data[i].frames);
            $("#speed").val(data[i].speed);
            $("#frame_range").attr("max", (data[i].frames)-1);
        }




        current = i;




        window.setTimeout(redraw, 50);
    }
});


$( document ).ready(function() {
    console.log( "ready!" );
    if (localStorage.jsonFile) {
        //$("#json_input").val(localStorage.jsonFile);
    } else {
        localStorage.jsonFile = "";
    }

    playerImage.src = "../core/assets/player/man_down.png";
    $("#hide_player").hide();
});