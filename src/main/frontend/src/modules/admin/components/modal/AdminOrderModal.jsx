import React, {useEffect} from 'react';

function AdminOrderModal(props) {
    const { closeModal, modalRef, modalHeader } = props;

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
                    <h1>{modalHeader}</h1>
                </div>
                <div className="modal-content-content">
                    {props.render()}
                </div>
            </div>
        </div>
    )
}

export default AdminOrderModal;