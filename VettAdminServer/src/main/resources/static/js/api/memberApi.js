async function getAllMembersProcess() {
    try{
        const response = await axios({
            method: "get",
            url: `/admin/api/members`,
        });
        if (response.status === 200){
            return await response.data;
        }
    }catch(error){
        swal(
            'Error!',
            '일시적 오류입니다. <br/> 다시 시도해주세요!',
            'error'
        );
    }
}

const passwordChangeProcess = async (passwordChangeRequest) => {
    try{
        console.log(passwordChangeRequest);
        const response = await axios({
            method: "patch",
            url: `/admin/api/change-password`,
            data : passwordChangeRequest
        });
        if (response.status === 200){
            return await response.data;
        }
    }catch(error){
        swal(
            'Error!',
            `Server Error <br/> ${error.response.data.message}`,
            'error'
        );
    }
};