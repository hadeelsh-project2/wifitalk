
function Show(){
    let password = document.getElementById('password');
    let img = document.getElementById('img');

    if(password.type === "password"){
        password.type = "text";
     
        img.src = "images/open.png";

    }else{
        password.type = "password";
        
        img.src = "images/close.png";
    }



}