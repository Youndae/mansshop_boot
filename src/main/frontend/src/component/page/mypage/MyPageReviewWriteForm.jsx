import React from 'react';
import DefaultBtn from "../../ui/DefaultBtn";

function MyPageReviewWriteForm(props) {
    const { productName, inputData, handleInputOnChange, handleSubmit } = props;

    return (
        <>
            <div className="mypage-qna-content">
                <div className="mypage-qna-write-title">
                    <span>상품명 : {productName}</span>
                </div>
            </div>
            <div className="mypage-qna-content-textarea">
                <textarea className="qna-content" name={'content'} onChange={handleInputOnChange} value={inputData}>{inputData}</textarea>
            </div>
            <div className="mypage-qna-content-btn">
                <DefaultBtn onClick={handleSubmit} btnText={'수정'}/>
            </div>
        </>
    )
}

export default MyPageReviewWriteForm;