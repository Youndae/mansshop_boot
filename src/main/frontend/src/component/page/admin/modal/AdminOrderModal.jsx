import React, {useEffect} from 'react';
import { useNavigate } from "react-router-dom";

function AdminOrderModal(props) {
    const { closeModal, modalRef } = props;

    useEffect(() => {
        document.body.style.cssText= `
            position: fixed;
            top: -${window.scrollY}px;
            overflow-y: scroll;
            width: 100%;
        `;


        document.addEventListener('mousedown', closeModal);
    }, []);

    return (
        <div className="modal-background">
            <div className="admin-modal-content" ref={modalRef}>
                <div className="modal-content-header">
                    <h1>주문 정보</h1>
                </div>
                <div className="modal-content-content">
                    {props.render()}
                </div>
            </div>
        </div>
    )
}

export default AdminOrderModal;