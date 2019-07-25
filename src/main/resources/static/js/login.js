// just for the demos, avoids form submit
jQuery.validator.setDefaults({
    debug: true,
    success: "valid"
});
$("#myform").validate({
    rules: {
        password: "required",
        confirmPassword: {
            equalTo: "#password"
        }
    }
});