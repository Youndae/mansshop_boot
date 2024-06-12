import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
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
                console.log('res : ', res);
                setClassification(res.data.classificationList);
                setClassificationId(res.data.classificationList[0].id);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('classification get Error : ', err);
            })
    }

    const handleSubmit = async () => {

        await axiosInstance.post('my-page/qna/member', {
            title: inputData.title,
            content: inputData.content,
            classificationId: classificationId,
        })
            .then(res => {
                console.log('member qna post res : ', res.data.id);

                navigate(`/my-page/qna/member/detail/${res.data.id}`);
            })
            .catch(err => {
                console.error('member qna post Error : ', err);
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