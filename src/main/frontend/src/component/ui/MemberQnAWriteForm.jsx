import React from "react";

function MemberQnAWriteForm(props) {
    const { inputData, classificationId, classification, handleInputOnChange, handleSelectOnChange, handleSubmit, btnText } = props;

    return (
        <div className="mypage-qna-content">
            <div className="mypage-qna-write-title">
                <label>제목 : </label>
                <input type={'text'} value={inputData.title} name={'title'} onChange={handleInputOnChange}/>
                <select className="classification-box" defaultValue={classificationId} value={classificationId} onChange={handleSelectOnChange}>
                    {classification.map((option, index) => {
                        return (
                            <MemberQnAClassificationOption
                                key={index}
                                option={option}
                            />
                        )
                    })}
                </select>
            </div>
            <div className="mypage-qna-content-textarea">
                <textarea className="qna-content" name={'content'} onChange={handleInputOnChange} value={inputData.content}>{inputData.content}</textarea>
            </div>
            <div className="mypage-qna-content-btn">
                <button type={'button'} onClick={handleSubmit}>{btnText}</button>
            </div>
        </div>
    )
}

function MemberQnAClassificationOption(props) {
    const { option } = props;

    return (
        <option value={option.id}>{option.name}</option>
    )
}

export default MemberQnAWriteForm;