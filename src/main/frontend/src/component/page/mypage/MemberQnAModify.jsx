import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";

import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import MemberQnAWriteForm from "../../ui/MemberQnAWriteForm";

// 문의 수정 페이지
function MemberQnAModify() {
    const [inputData, setInputData] = useState({
        title: '',
        content: '',
    });
    const [classification, setClassification] = useState([]);
    const [classificationId, setClassificationId] = useState('');
    const { qnaId } = useParams();

    const navigate = useNavigate();

    useEffect(() => {
        getPatchData();
    }, [qnaId]);

    //수정할 문의 데이터 조회
    const getPatchData = async () => {
        await axiosInstance.get(`my-page/qna/member/modify/${qnaId}`)
            .then(res => {
                setInputData({
                    title: res.data.content.qnaTitle,
                    content: res.data.content.qnaContent
                });

                setClassificationId(res.data.content.qnaClassificationId);
                setClassification(res.data.content.classificationList);
            })
    }

    //문의 수정 이벤트
    const handleSubmit = async () => {

        await axiosInstance.patch('my-page/qna/member', {
            qnaId: qnaId,
            title: inputData.title,
            content: inputData.content,
            classificationId: classificationId,
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    navigate(`/my-page/qna/member/detail/${qnaId}`);
            })
    }

    //문의 분류 select box 이벤트
    const handleSelectOnChange = (e) => {
        const selectValue = e.target.value;
        setClassificationId(selectValue);
    }

    //제목, textarea 입력 이벤트
    const handleInputOnChange = (e) => {
        setInputData({
            ...inputData,
            [e.target.name]: e.target.value,
        })
    }

    return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={true}
            />
            <div className="mypage-content">
                <div className="mypage-qna-header">
                    <h1>문의하기</h1>
                </div>
                <MemberQnAWriteForm
                    inputData={inputData}
                    classificationId={classificationId}
                    classification={classification}
                    handleInputOnChange={handleInputOnChange}
                    handleSelectOnChange={handleSelectOnChange}
                    handleSubmit={handleSubmit}
                    btnText={'수정'}
                />
            </div>
        </div>
    )

}

export default MemberQnAModify;