function password_hash(form, password) {
    // Create a new element input, this will be our hashed password field. 
    var p = document.createElement("input");
 
    // Add the new element to our form. 
    form.appendChild(p);
    p.name = "password";
    p.type = "hidden";
    p.value = SHA512(password.value + "IrpiP8JbDCH1N9hLbvtG2A/XjH+HR1e5");
 
    // Make sure the plaintext password doesn't get sent. 
    password.value = "";
 
    // Finally submit the form. 
    form.submit();
}