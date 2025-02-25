const hideErrorMessage = () => {

    const errorMessageWrapper = document.querySelector(".error-message-wrapper");
    if (errorMessageWrapper){
        errorMessageWrapper.classList.add("fade-out");

        errorMessageWrapper.addEventListener("animationend", () => {
            errorMessageWrapper.style.display = "none";
        });
    }
};