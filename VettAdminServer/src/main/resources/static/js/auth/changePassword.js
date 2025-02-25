const hideErrorMessage = () => {

    const errorWrapper = document.querySelector(".error-message-wrapper");
    if (errorWrapper && errorWrapper.style.display === "block") {
        errorWrapper.classList.remove("fade-in");
        errorWrapper.classList.add("fade-out");
        errorWrapper.addEventListener("animationend", function handler() {
            errorWrapper.style.display = "none";
            errorWrapper.classList.remove("fade-out");
            errorWrapper.removeEventListener("animationend", handler);
        });
    }
};

const passwordRegexp = /^(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;

if (document.querySelector(".change-password-form")){
    document.querySelector(".change-password-form").addEventListener("submit", async function(event) {
        event.preventDefault();

        const submitButton = document.querySelector(".submit-button");
        submitButton.disabled = true;
        submitButton.innerText = "Submit...";

        const oldPassword = document.querySelector("#oldPassword").value;
        const newPassword = document.querySelector("#newPassword").value;
        const confirmNewPassword = document.querySelector("#confirmNewPassword").value;

        let errorMessage;
        if (oldPassword.trim().length === 0) {
            errorMessage = "기존 관리자 비밀번호를 입력해 주세요.";
        } else if (newPassword.trim().length === 0 || !passwordRegexp.test(newPassword)) {
            errorMessage = "새 관리자 비밀번호를 입력해 주세요. \n비밀번호는 8자 이상, 숫자와 특수문자를 포함해야 합니다.";
        } else if (newPassword !== confirmNewPassword) {
            errorMessage = "확인용 비밀번호가 비어있거나 새 비밀번호와 일치하지 않습니다.";
        }

        if (errorMessage) {
            const errorWrapper = document.querySelector(".error-message-wrapper");
            errorWrapper.classList.remove("fade-out");
            document.querySelector('.error-message').innerText = errorMessage;

            void errorWrapper.offsetWidth;
            errorWrapper.style.display = "block";
            errorWrapper.classList.add("fade-in");

            submitButton.disabled = false;
            submitButton.innerText = "Update Password";

            return;
        }
        const passwordChangeResponse = await passwordChangeProcess({
            oldPassword, newPassword
        });

        if (passwordChangeResponse &&
            passwordChangeResponse.success &&
            passwordChangeResponse.data){

            notyf.success('Success change admin password. \n Please login again.');
            setTimeout(() => {
                location.href="/admin/auth/logout";
            },1500)
        }else {
            submitButton.disabled = false;
            submitButton.innerText = "Update Password";
        }
    });
}
