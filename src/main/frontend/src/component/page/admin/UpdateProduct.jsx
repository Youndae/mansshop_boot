import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useParams} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import AddProductForm from "./AddProductForm";
import {setMemberObject} from "../../../modules/loginModule";
import {imageInputChange, imageValidation, setProductFormData} from "../../../modules/imageModule";


/*
        상품 수정 컴포넌트.

        AddProduct와 같은 구조로 처리될 것이며
        상품 조회로 값을 하위 컴포넌트에 전달해 처리힌다.

        헤더에는 상품 수정이라는 타이틀과 수정 버튼만 배치한다.
     */
function UpdateProduct() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const { productId } = useParams();
    const [productData, setProductData] = useState({
        classification: '',
        productName: '',
        price: '',
        isOpen: false,
        discount: 0,
    });
    const [firstThumbnail, setFirstThumbnail] = useState('');
    const [newFirstThumbnail, setNewFirstThumbnail] = useState('');
    const [deleteFirstThumbnail, setDeleteFirstThumbnail] = useState('');
    const [thumbnail, setThumbnail] = useState([]);
    const [newThumbnail, setNewThumbnail] = useState([]);
    const [deleteThumbnail, setDeleteThumbnail] = useState([]);
    const [infoImage, setInfoImage] = useState([]);
    const [newInfoImage, setNewInfoImage] = useState([]);
    const [deleteInfoImage, setDeleteInfoImage] = useState([]);
    const [classification, setClassification] = useState([]);
    const [optionList, setOptionList] = useState([]);
    const [deleteOption, setDeleteOption] = useState([]);
    const [infoImageLength, setInfoImageLength] = useState(0);

    const navigate = useNavigate();
    const dispatch = useDispatch();

    useEffect(() => {
        getPatchData(productId);
    }, [productId]);

    const getPatchData = async (productId) => {
        await axiosInstance.get(`admin/product/patch/${productId}`)
            .then(res => {
                console.log('update res : ', res);
                const content = res.data;

                setProductData({
                    classification: content.classificationId,
                    productName: content.productName,
                    price: content.price,
                    isOpen: content.isOpen,
                    discount: content.discount,
                });

                setFirstThumbnail(content.firstThumbnail);
                setThumbnail(content.thumbnailList);
                setInfoImage(content.infoImageList);
                setClassification(content.classificationList);
                setOptionList(content.optionList);
                setInfoImageLength(content.infoImageList.length);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('getPatchProduct error : ', err);
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
        const optionId = e.target.name;

        if(optionId !== ''){
            const deleteOptionArr = [...deleteOption];
            deleteOptionArr.push(optionId);
            setDeleteOption(deleteOptionArr);
        }

        const optionArr = [...optionList];
        optionArr.splice(idx, 1);
        setOptionList(optionArr);
    }

    /*
        req data
        productId -> QueryString
        formData
            productData.productName
            productData.classification
            productData.price
            productData.isOpen
            productData.discount

            optionList
                optionId
                size
                color
                optionStock
                optionIsOpen
            deleteOptionId[]

            newFirstThumbnail
            deleteFirstThumbnail
            newThumbnail[]
            deleteThumbnail[]
            newInfoImage[]
            deleteInfoImage[]


       module getFormData()
            productData
            optionList
            newFirstThumbnail
            newThumbnail
            newInfoImage

       update FormData
            deleteOptionId[]
            deleteFirstThumbnail
            deleteThumbnail[]
            deleteInfoImage[]

     */
    const handleSubmitOnClick = async (e) => {
        e.preventDefault();

        console.log('productData : ', productData);

        let formData = setProductFormData(productData, optionList, newFirstThumbnail, newThumbnail, newInfoImage);


        deleteOption.forEach(deleteOptionId => formData.append('deleteOptionList', deleteOptionId));

        if(deleteFirstThumbnail !== '')
            formData.append('deleteFirstThumbnail', deleteFirstThumbnail);
        deleteThumbnail.forEach(file => formData.append('deleteThumbnail', file));
        deleteInfoImage.forEach(file => formData.append('deleteInfoImage', file));

        for(let key of formData.keys())
            console.log(key, " : ", formData.get(key));


        await axiosInstance.patch(`admin/product/${productId}`, formData, {
            headers: {
                'Content-Type' : 'multipart/form-data'
            }
        })
            .then(res => {
                navigate(`/admin/product/${res.data.id}`);
            })
            .catch(err => {
                console.error('patchProduct Error : ', err);
            })

    }

    const handleSelectOnChange = (e) => {
        const value = e.target.value;

        setProductData({
            ...productData,
            classification: value,
        })
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

    const handleRemoveOriginalFirstThumbnail = (e) => {
        setDeleteFirstThumbnail(firstThumbnail);
        setFirstThumbnail('');
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

    const handleRemoveOriginalThumbnail = (e) => {
        const deleteIdx = e.target.value;

        const files = [...thumbnail];
        const deleteFiles = [...deleteThumbnail];
        deleteFiles.push(files[deleteIdx]);
        files.splice(deleteIdx, 1);

        console.log('handleRemoveOriginThumb :: deleteFilesArr : ', deleteFiles);

        setDeleteThumbnail(deleteFiles);
        setThumbnail(files);
    }

    const handleInfoImageInputChange = (e) => {
        const files = imageInputChange(e, newInfoImage);

        setNewInfoImage(files);
        setInfoImageLength(infoImageLength + 1);
    }

    const handleRemoveInfoImage = (e) => {
        const deleteIdx = e.target.value;

        const files = [...newInfoImage];

        files.splice(deleteIdx, 1);

        setNewInfoImage(files);
        setInfoImageLength(infoImageLength - 1);
    }

    const handleRemoveOriginalInfoImage = (e) => {
        const deleteIdx = e.target.value;

        const files = [...infoImage];
        const deleteFiles = [...deleteInfoImage];
        deleteFiles.push(files[deleteIdx]);
        files.splice(deleteIdx, 1);
        setDeleteInfoImage(deleteFiles);
        setInfoImage(files);
        setInfoImageLength(infoImageLength - 1);
    }


    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'product'}
            />
            <AddProductForm
                productData={productData}
                optionList={optionList}
                headerText={'상품 수정'}
                handleProductOnChange={handleProductOnChange}
                handleAddOption={handleAddOption}
                handleRemoveOption={handleRemoveOption}
                handleOptionOnChange={handleOptionOnChange}
                submitBtnText={'수정'}
                handleSubmitOnClick={handleSubmitOnClick}
                handleOptionRadioOnChange={handleOptionRadioOnChange}
                firstThumbnail={firstThumbnail}
                newFirstThumbnail={newFirstThumbnail}
                thumbnail={thumbnail}
                newThumbnail={newThumbnail}
                infoImage={infoImage}
                newInfoImage={newInfoImage}
                infoImageLength={infoImageLength}
                classification={classification}
                handleSelectOnChange={handleSelectOnChange}
                handleFirstThumbnailInputChange={handleFirstThumbnailInputChange}
                handleRemoveFirstThumbnail={handleRemoveFirstThumbnail}
                handleRemoveOriginalFirstThumbnail={handleRemoveOriginalFirstThumbnail}
                handleThumbnailInputChange={handleThumbnailInputChange}
                handleRemoveThumbnail={handleRemoveThumbnail}
                handleRemoveOriginalThumbnail={handleRemoveOriginalThumbnail}
                handleInfoImageInputChange={handleInfoImageInputChange}
                handleRemoveInfoImage={handleRemoveInfoImage}
                handleRemoveOriginalInfoImage={handleRemoveOriginalInfoImage}
            />
        </div>
    )

}

export default UpdateProduct;