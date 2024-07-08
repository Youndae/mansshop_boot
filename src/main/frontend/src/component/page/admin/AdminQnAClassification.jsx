import React, {useState, useEffect} from 'react';
import {useDispatch, useSelector} from "react-redux";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import DefaultBtn from "../../ui/DefaultBtn";

/*
        AdminClassification과 동일한 구조로 처리.
     */

function AdminQnAClassification() {
    const loginStatus = useSelector((state) => state.member.loginStatus);

    const [data, setData] = useState([]);
    const [inputStatus, setInputStatus] = useState(false);
    const [inputValue, setInputValue] = useState('');

    const dispatch = useDispatch();

    useEffect(() => {
        getQnAClassification();
    }, []);

    const getQnAClassification = async () => {

        await axiosInstance.get(`admin/qna/classification`)
            .then(res => {
                setData(res.data.content);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handleAddBtn = () => {
        setInputStatus(!inputStatus);
    }

    const handleOnChange = (e) => {
        setInputValue(e.target.value);
    }

    const handleSubmit = async () => {

        await axiosInstance.post(`admin/qna/classification`, {
            name: inputValue
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(res => {
                if(checkResponseMessageOk(res)) {
                    getQnAClassification();
                    setInputStatus(false);
                    setInputValue('');
                }
            })
    }

    const handleDelete = async (e) => {
        const classificationName = e.target.value;

        await axiosInstance.delete(`admin/qna/classification/${classificationName}`)
            .then(res => {
                if(checkResponseMessageOk(res))
                    getQnAClassification();
            })
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'qna'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>문의 카테고리 설정</h1>
                    <QnAClassificationHeaderBtn
                        status={inputStatus}
                        handleAddBtn={handleAddBtn}
                    />
                </div>
                <div className="admin-content-content admin-qna-classification-content">
                    <QnAClassificationInput
                        status={inputStatus}
                        value={inputValue}
                        onChange={handleOnChange}
                        onClick={handleSubmit}
                    />
                    {data.map((value, index) => {
                        return (
                            <div key={index} className="admin-qna-classification">
                                <div className="admin-qna-classification-info">
                                    <label>카테고리명 : </label>
                                    <span className="admin-qna-classification-name">{value.name}</span>
                                </div>
                                <div className="admin-qna-classification-delete-btn">
                                    <DefaultBtn
                                        btnText={'카테고리 삭제'}
                                        onClick={handleDelete}
                                        value={value.id}
                                    />
                                </div>
                            </div>
                        )
                    })}
                </div>
            </div>
        </div>
    )
}

function QnAClassificationInput(props) {
    const { status, inputValue, onChange, onClick } = props;

    if(status) {
        return (
            <div className="admin-qna-classification-input">
                <div className="classification-input-content">
                    <label>카테고리명 : </label>
                    <input type={'text'} className="admin-classification-input" value={inputValue} onChange={onChange}/>
                </div>
                <div className="classification-input-btn">
                    <DefaultBtn
                        btnText={'추가'}
                        className={'classification-submit-btn'}
                        onClick={onClick}
                    />
                </div>
            </div>
        )
    }else{
        return null;
    }
}

function QnAClassificationHeaderBtn (props) {
    const { status, handleAddBtn } = props;

    if(status){
        return (
            <DefaultBtn
                btnText={'닫기'}
                className={'admin-qna-classification-add-btn'}
                onClick={handleAddBtn}
            />
        )
    }else {
        return (
            <DefaultBtn
                btnText={'카테고리 추가'}
                className={'admin-qna-classification-add-btn'}
                onClick={handleAddBtn}
            />
        )
    }
}

export default AdminQnAClassification;