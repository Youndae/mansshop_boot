import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";

import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import MemberQnAWriteForm from "../../ui/MemberQnAWriteForm";

function MemberQnAWrite() {
    const loginStatus = useSelector((state) => state.member.loginStatus);

    const [inputData, setInputData] = useState({
        title: '',
        content: '',
    });
    const [classification, setClassification] = useState([]);
    const [classificationId, setClassificationId] = useState('');

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getQnAClassification();
    }, []);

    const getQnAClassification = async () => {

        await axiosInstance.get('my-page/classification')
            .then(res => {
                setClassification(res.data.classificationList);
                setClassificationId(res.data.classificationList[0].id);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handleSubmit = async () => {

        await axiosInstance.post('my-page/qna/member', {
            title: inputData.title,
            content: inputData.content,
            classificationId: classificationId,
        })
            .then(res => {
                navigate(`/my-page/qna/member/detail/${res.data.id}`);
            })
    }

    const handleSelectOnChange = (e) => {
        const selectValue = e.target.value;
        setClassificationId(selectValue);
    }

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
                    btnText={'작성'}
                />
            </div>
        </div>
    )
}

export default MemberQnAWrite;