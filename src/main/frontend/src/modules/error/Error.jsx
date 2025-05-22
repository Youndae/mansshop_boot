import React from 'react';

/*
    오류 페이지
    403, 404 등 오류가 발생하는 경우 이 컴포넌트가 호출
 */
function Error() {

    return (
        <div className='error-page-content'>
            <h2>오류가 발생했거나 존재하지 않는 페이지 요청입니다.</h2>
            <h2>문제가 계속된다면 관리자에게 문의해주세요.</h2>
        </div>
    )
}

export default Error;