import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import MemberQnAWriteForm from "../../ui/MemberQnAWriteForm";

function MemberQnAModify() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [inputData, setInputData] = useState({
        title: '',
        content: '',
    });
    const [classification, setClassification] = useState([]);
    const [classificationId, setClassificationId] = useState('');
    const { qnaId } = useParams();

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getPatchData();
    }, [qnaId]);

    const getPatchData = async () => {
        await axiosInstance.get(`my-page/qna/member/modify/${qnaId}`)
            .then(res => {

                setInputData({
                    title: res.data.content.qnaTitle,
                    content: res.data.content.qnaContent
                });

                setClassificationId(res.data.content.qnaClassificationId);
                setClassification(res.data.content.classificationList);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('memberQnA getPatch Error : ', err);
            })
    }

    const handleSubmit = async () => {

        await axiosInstance.patch('my-page/qna/member', {
            qnaId: qnaId,
            title: inputData.title,
            content: inputData.content,
            classificationId: classificationId,
        })
            .then(res => {
                console.log('member qna patch res : ', res);

                if(res.data.message === 'OK')
                    navigate(`/my-page/qna/member/detail/${qnaId}`);
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
                    btnText={'수정'}
                />
            </div>
        </div>
    )

}

export default MemberQnAModify;