import React from "react";
import {useNavigate} from "react-router-dom";

function QnADetail(props) {
    const { data
        , replyData
        , nickname
        , handleReplyModifyOpen
        , handleReplyModifyClose
        , handleModifyOnChange
        , modifyTextValue
        , handleModifySubmit
        , handleInputOnChange
        , inputValue
        , handleInputSubmit
        , titleText
        , type
    } = props;

    const navigate = useNavigate();

    const handleModifyBtn = () => {
        const qnaId = data.memberQnAId;

        navigate(`/my-page/qna/member/update/${qnaId}`);
    }

    return (
        <div className="mypage-content">
            <div className="mypage-qna-header">
                <h1>{titleText}</h1>
            </div>
            <div className="mypage-qna-content">
                <div className="mypage-qna-content-header-btn">
                    <HeaderBtn
                        status={data.qnaStatus}
                        type={type}
                        handleModifyBtn={handleModifyBtn}
                    />
                </div>
                <div className="mypage-qna-content-title">
                    <QnATitle data={data}/>
                </div>
                <div className="mypage-qna-content-content">
                    <p className="qna-detail-content">
                        {data.qnaContent}
                    </p>
                </div>
                <div className="qna-reply-input">
                    <textarea className="reply-input-textarea" onChange={handleInputOnChange} value={inputValue}>{inputValue}</textarea>
                    <button type={'button'} onClick={handleInputSubmit}>작성</button>
                </div>
                <div className="mypage-qna-content-reply">
                    {replyData.map((reply, index) => {
                        return (
                            <MyPageQnADetailReply
                                key={index}
                                data={reply}
                                username={nickname}
                                index={index}
                                handleReplyModifyOpen={handleReplyModifyOpen}
                                handleReplyModifyClose={handleReplyModifyClose}
                                handleModifyOnChange={handleModifyOnChange}
                                modifyTextValue={modifyTextValue}
                                handleModifySubmit={handleModifySubmit}
                            />
                        )
                    })}
                </div>
            </div>
        </div>
    )
}

function HeaderBtn(props) {
    const { status, type, handleModifyBtn } = props;

    if(type !== undefined && status === 0){
        return (
            <>
                <button className={'header-btn-modify'} onClick={handleModifyBtn} type={'button'}>수정</button>
                <button className={'header-btn-delete'} type={'button'}>삭제</button>
            </>
        )
    }else {
        return (
            <button className={'header-btn-delete'} type={'button'}>삭제</button>
        )
    }
}

function QnATitle(props) {
    const { data } = props;
    let statusText = '답변 완료';
    if(data.qnaStatus === 0)
        statusText = '미답변';

    return (
        <>
            <h2>{data.title}({statusText})</h2>
            <small className="qna-reply-date">{data.date}</small>
        </>
    )
}

function MyPageQnADetailReply(props) {
    const { data, username, index, handleReplyModifyOpen, handleReplyModifyClose, handleModifyOnChange, modifyTextValue, handleModifySubmit } = props;

    if(username === data.writer){
        return (
            <div className="qna-detail-reply">
                <div className="qna-reply-writer">
                    <strong>{data.writer}</strong>
                    <small className="qna-reply-date">{data.updatedAt}</small>
                </div>
                <div className="qna-reply-content">
                    <p>{data.replyContent}</p>
                    <QnAModifyButton
                        index={index}
                        handleReplyModifyOpen={handleReplyModifyOpen}
                        handleReplyModifyClose={handleReplyModifyClose}
                        inputStatus={data.inputStatus}
                    />
                </div>
                <QnAModifyArea
                    index={index}
                    handleModifyOnChange={handleModifyOnChange}
                    modifyTextValue={modifyTextValue}
                    handleModifySubmit={handleModifySubmit}
                    inputStatus={data.inputStatus}
                />
            </div>
        )
    }else {
        return (
            <div className="qna-detail-reply">
                <div className="qna-reply-writer">
                    <strong>{data.writer}</strong>
                    <small className="qna-reply-date">{data.updatedAt}</small>
                </div>
                <div className="qna-reply-content">
                    <p>{data.replyContent}</p>
                </div>
            </div>
        )
    }


}

function QnAModifyButton(props) {
    const { index, handleReplyModifyOpen, handleReplyModifyClose, inputStatus } = props;

    if(inputStatus) {
        return (
            <button type={'button'} onClick={handleReplyModifyClose} value={index}>닫기</button>
        )
    }else {
        return (
            <button type={'button'} onClick={handleReplyModifyOpen} value={index}>댓글 수정</button>
        )
    }
}

function QnAModifyArea(props) {
    const { index, handleModifyOnChange, modifyTextValue, handleModifySubmit, inputStatus } = props;

    if(inputStatus) {
        return (
            <div className="qna-reply-modify-input">
                <textarea onChange={handleModifyOnChange} className="qna-reply-modify-text" value={modifyTextValue}>{modifyTextValue}</textarea>
                <div className="qna-reply-modify-btn">
                    <button type={'button'} onClick={handleModifySubmit} value={index}>수정</button>
                </div>
            </div>
        )
    }else {
        return null;
    }
}

export default QnADetail;