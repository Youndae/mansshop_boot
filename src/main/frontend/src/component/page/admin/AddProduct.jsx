import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import {imageInputChange, imageValidation, setProductFormData} from "../../../modules/imageModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import AddProductForm from "./AddProductForm";

/*
    상품 추가 컴포넌트

    처리 데이터
        상품 분류
        상품명
        가격
        공개 여부
        할인율
        옵션
        대표 썸네일
        썸네일
        상품 정보 이미지
 */
function AddProduct() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [productData, setProductData] = useState({
        classification: 'default',
        productName: '',
        price: '',
        isOpen: false,
        discount: 0,
    });
    const [newFirstThumbnail, setNewFirstThumbnail] = useState('');
    const [newThumbnail, setNewThumbnail] = useState([]);
    const [newInfoImage, setNewInfoImage] = useState([]);
    const [classification, setClassification] = useState([]);
    const [optionList, setOptionList] = useState([]);
    const [infoImageLength, setInfoImageLength] = useState(0);

    const navigate = useNavigate();
    const dispatch = useDispatch();

    useEffect(() => {
        getClassification();
    }, []);

    const getClassification = async () => {
        await axiosInstance.get(`admin/product/classification`)
            .then(res => {
                setClassification(res.data.content);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handleProductOnChange = (e) => {
        let value = e.target.value;

        if(e.target.name === 'price' || e.target.name === 'discount')
            value = Number(e.target.value);
        else if(e.target.name === 'isOpen')
            value = !productData.isOpen;

        setProductData({
            ...productData,
            [e.target.name] : value,
        });
    }

    const handleOptionOnChange = (e) => {
        const idx = e.target.parentElement.parentElement.getAttribute('value');
        let value = e.target.value;

        if(e.target.name === 'optionStock')
            value = Number(value);

        optionList[idx] = {
            ...optionList[idx],
            [e.target.name]: value,
        };

        setOptionList([...optionList]);
    }

    const handleOptionRadioOnChange = (e) => {
        const radioName = e.target.name.split('/');
        const name = radioName[0];
        const idx = radioName[1];
        const value = !optionList[idx].optionIsOpen;

        optionList[idx] = {
            ...optionList[idx],
            [name]: value
        };

        setOptionList([...optionList]);
    }

    const handleAddOption = () => {
        const optionArr = [...optionList];

        optionArr.push({
            optionId: 0,
            size: '',
            color: '',
            optionStock: '',
            optionIsOpen: true
        });

        setOptionList(optionArr);
    }

    const handleRemoveOption = (e) => {
        const idx = e.target.value;

        const optionArr = [...optionList];

        optionArr.splice(idx, 1);

        setOptionList(optionArr);
    }

    const handleSubmitOnClick = async () => {
        const formData = setProductFormData(productData, optionList, newFirstThumbnail, newThumbnail, newInfoImage);

        await axiosInstance.post(`admin/product`, formData, {
            headers: {
                'Content-Type' : 'multipart/form-data'
            }
        })
            .then(res => {
                navigate(`/admin/product/${res.data.id}`);
            })
    }

    const handleSelectOnChange = (e) => {
        setProductData({
            ...productData,
            classification: e.target.value
        });
    }

    const handleFirstThumbnailInputChange = (e) => {
        if(imageValidation(e)){
            const file = e.target.files[0];
            setNewFirstThumbnail(file);
        }
    }

    const handleRemoveFirstThumbnail = (e) => {
        window.URL.revokeObjectURL(newFirstThumbnail);
        setNewFirstThumbnail('');
    }

    const handleThumbnailInputChange = (e) => {
        const files = imageInputChange(e, newThumbnail);

        setNewThumbnail(files);
    }

    const handleRemoveThumbnail = (e) => {
        const deleteIdx = e.target.value;
        const files = [...newThumbnail];

        files.splice(deleteIdx, 1);

        setNewThumbnail(files);
    }

    const handleInfoImageInputChange = (e) => {
        const files = imageInputChange(e, newInfoImage);

        setNewInfoImage(files);
        setInfoImageLength(files.length);
    }

    const handleRemoveInfoImage = (e) => {
        const deleteIdx = e.target.value;
        const files = [...newInfoImage];

        files.splice(deleteIdx, 1);

        setNewInfoImage(files);
        setInfoImageLength(files.length);
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'product'}
            />
            <AddProductForm
                productData={productData}
                optionList={optionList}
                headerText={'상품 등록'}
                handleProductOnChange={handleProductOnChange}
                handleAddOption={handleAddOption}
                handleRemoveOption={handleRemoveOption}
                handleOptionOnChange={handleOptionOnChange}
                submitBtnText={'추가'}
                handleSubmitOnClick={handleSubmitOnClick}
                handleOptionRadioOnChange={handleOptionRadioOnChange}
                firstThumbnail={[]}
                newFirstThumbnail={newFirstThumbnail}
                thumbnail={[]}
                newThumbnail={newThumbnail}
                infoImage={[]}
                newInfoImage={newInfoImage}
                infoImageLength={infoImageLength}
                classification={classification}
                handleSelectOnChange={handleSelectOnChange}
                handleFirstThumbnailInputChange={handleFirstThumbnailInputChange}
                handleRemoveFirstThumbnail={handleRemoveFirstThumbnail}
                handleThumbnailInputChange={handleThumbnailInputChange}
                handleRemoveThumbnail={handleRemoveThumbnail}
                handleInfoImageInputChange={handleInfoImageInputChange}
                handleRemoveInfoImage={handleRemoveInfoImage}
            />
        </div>
    )
}

export default AddProduct;