import React, {useEffect} from 'react';
import styled from "styled-components";
import DefaultBtn from "../../ui/DefaultBtn";
import {useNavigate} from "react-router-dom";

const ModalContent = styled.div`
    width: 500px;
    height: 300px;
    z-index: 150;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    border-radius: 10px;
    box-shadow: 2px 2px 2px rgba(0, 0, 0, 0.25);
    background-color: white;
    justify-content: center;
    overflow: auto;
    border: 1px solid black;
`

const ModalBackground = styled.div`
    position: fixed;
      top: 0;
      left: 0;
      bottom: 0;
      right: 0;
      background-color: rgba(0, 0, 0, 0.4);
      z-index: 0;
      cursor: auto;
`

function Modal(props) {
    const { closeModal, data, modalRef } = props;

    const navigate = useNavigate();

    useEffect(() => {
        document.body.style.cssText= `
            position: fixed;
            top: -${window.scrollY}px;
            overflow-y: scroll;
            width: 100%;
        `;


        document.addEventListener('mousedown', closeModal);
    }, []);

    const handleModifyBtn = () => {
        navigate(`/my-page/review/modify/${data.reviewId}`);
    }

    return (
        <ModalBackground>
            <ModalContent className="modal" ref={modalRef}>
                <div className="modal-header">
                    <h2>{data.productName}</h2>
                    <DefaultBtn
                        onClick={handleModifyBtn}
                        btnText={'수정'}
                        value={data.reviewId}
                    />
                </div>
                <div className="modal-content">
                    <div className="modal-review">
                        {data.content}
                    </div>
                    <ReviewReply
                        data={data}
                    />
                </div>
            </ModalContent>
        </ModalBackground>
    )
}

function ReviewReply(props) {
    const { data } = props;

    if(data.replyContent !== null) {
        return (
            <div className="modal-review-reply-content">
                <div className="modal-reply-header">
                    <strong>관리자</strong>
                    <small>{data.replyUpdatedAt}</small>
                </div>
                <div className="modal-reply-content">
                    <span>{data.replyContent}</span>
                </div>
            </div>
        )
    }else{
        return null;
    }
}

export default Modal;