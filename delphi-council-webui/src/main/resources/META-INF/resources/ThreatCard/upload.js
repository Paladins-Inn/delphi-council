/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

var totalSize = 0; // Enthält die Gesamtgröße aller hochzuladenden Dateien
var totalProgress = 0; // Enthält den aktuellen Gesamtfortschritt
var currentUpload = null; // Enthält die Datei, die aktuell hochgeladen wird

function allowDrop(ev) {
    ev.preventDefault();
}

function handleDropEvent(event) {
    event.stopPropagation();
    event.preventDefault();

    uploadFile(event.dataTransfer.files[0]);
}

function uploadFile(file) {
    const reader = new FileReader();

    reader.readAsDataURL(file)
    reader.onload = function (e) {
        document.getElementById("card").src = e.target.result;
    };

    setTimeout(function () {
        DrawCard();
    }, 500);
}

function DrawCard(){
    var c = document.getElementById("myCanvas");
    var ctx = c.getContext("2d");
    ctx.clearRect(0, 0, c.width, c.height);
    ctx.drawImage(document.getElementById("card"), 0, 0);
    ctx.drawImage(document.getElementById("imgFrame"), 0, 0);
    if (jQuery("#sizeCheckbox2").prop("checked")) ctx.drawImage(document.getElementById("imgLarge2"), 0, 0);
    if (jQuery("#sizeCheckbox4").prop("checked")) ctx.drawImage(document.getElementById("imgLarge4"), 0, 0);
    //ctx.fillStyle = 'white';

    //ctx.fillText("EINSCHÜCHTERN", 68, 103);
    //ctx.strokeStyle = "black";
    //ctx.strokeText("EINSCHÜCHTERN", 68, 103);
    lbl(ctx, "EINSCHÜCHTERN", 68, 100);
    lbl(ctx, "MANÖVRIEREN", 68, 134);
    lbl(ctx, "VERSPOTTEN", 68, 167);
    lbl(ctx, "TRICKSEN", 68, 203);
    lbl(ctx, "NAHKAMPF", 226, 512);
    lbl(ctx, "AUSWEICHEN", 211, 546);
    lbl(ctx, "WAFFENLOS", 218, 581);
    lbl(ctx, "ROBUSTHEIT", 213, 615);

    lbl(ctx, jQuery("#txtName").val(), 140 + parseInt(jQuery("#ofName").val()), 50);
    va(ctx,jQuery("#txtEinschuechtern").val(), 38 + parseInt(jQuery("#ofEinschuechtern").val()),101);
    va(ctx,jQuery("#txtManoevrieren").val(), 38 + parseInt(jQuery("#ofManoevrieren").val()),136);
    va(ctx,jQuery("#txtVerspotten").val(), 38 + parseInt(jQuery("#ofVerspotten").val()),171);
    va(ctx,jQuery("#txtTricksen").val(), 38 + parseInt(jQuery("#ofTricksen").val()),205);
    va(ctx,jQuery("#txtNahkampf").val(), 298 + parseInt(jQuery("#ofNahkampf").val()),513);
    va(ctx,jQuery("#txtAusweichen").val(), 298 + parseInt(jQuery("#ofAusweichen").val()),548);
    va(ctx,jQuery("#txtWaffenlos").val(), 298 + parseInt(jQuery("#ofWaffenlos").val()),583);
    va(ctx,jQuery("#txtRobustheit").val(), 298 + parseInt(jQuery("#ofRobustheit").val()),617);
}

function lbl(ctx, stText,x,y) {
    ctx.font = "12px Arial";
    ctx.shadowColor="black";
    ctx.shadowBlur=7;
    ctx.lineWidth=5;
    ctx.strokeText(stText, x, y);
    ctx.shadowBlur=0;
    ctx.fillStyle="white";
    ctx.fillText(stText, x, y);
}

function va(ctx, stText,x,y) {
    ctx.font = "18px Arial bold";
    ctx.shadowBlur=0;
    ctx.fillStyle="black";
    ctx.lineWidth=2;
    ctx.fillText(stText, x, y,24);
    ctx.strokeText(stText, x, y,24);
}

/* function to_image(){
    var canvas = document.getElementById("myCanvas");
    document.getElementById("theimage").src = canvas.toDataURL();
    Canvas2Image.saveAsPNG(canvas);
} */
