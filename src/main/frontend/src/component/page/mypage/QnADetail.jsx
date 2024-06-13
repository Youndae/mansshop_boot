import React from "react";
import {useNavigate} from "react-router-dom";
import DefaultBtn from "../../ui/DefaultBtn";

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
        , handleDeleteBtn
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
                        handleDeleteBtn={handleDeleteBtn}
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
                    <DefaultBtn onClick={handleInputSubmit} btnText={'작성'}/>
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
    const { status, type, handleModifyBtn, handleDeleteBtn } = props;

    if(type !== undefined && status === 0){
        return (
            <>
                <DefaultBtn className={'header-btn-modify'} onClick={handleModifyBtn} btnText={'수정'}/>
                <DefaultBtn className={'header-btn-delete'} onClick={handleDeleteBtn} btnText={'삭제'}/>
            </>
        )
    }else {
        return (
            <DefaultBtn className={'header-btn-delete'} onClick={handleDeleteBtn} btnText={'삭제'}/>
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
            <DefaultBtn onClick={handleReplyModifyClose} value={index} btnText={'닫기'}/>
        )
    }else {
        return (
            <DefaultBtn onClick={handleReplyModifyOpen} value={index} btnText={'댓글 수정'}/>
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
                    <DefaultBtn onClick={handleModifySubmit} value={index} btnText={'수정'}/>
                </div>
            </div>
        )
    }else {
        return null;
    }
}

export default QnADetail;