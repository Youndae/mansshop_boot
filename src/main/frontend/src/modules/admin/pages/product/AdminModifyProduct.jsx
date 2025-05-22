import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import {
	getPatchProductData,
	patchProduct
} from '../../services/adminProductService';
import { imageInputChange, imageValidation } from '../../../../common/utils/ImageUtils';

import AdminSideNav from '../../components/AdminSideNav';
import AdminAddProductForm from '../../components/AdminAddProductForm';

/*
	상품 수정 페이지
	상품 추가와 마찬가지로 AddProductForm Component 사용.

	헤더에는 상품 수정이라는 타이틀과 수정 버튼만 배치.
*/
function AdminModifyProduct() {
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

	useEffect(() => {
		// 수정할 상품 데이터 조회
		const getPatchData = async (productId) => {
			try {
				const res = await getPatchProductData(productId);

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
			} catch(err) {
				console.log(err);
			}
		}

		getPatchData(productId);
	}, [productId]);
	
	// 상품 정보 데이터 input 입력 이벤트
	const handleProductOnChange = (e) => {
		let { name, value } = e.target;

		if(name === 'price' || name === 'discount')
			value = Number(value);
		else if(name === 'isOpen')
			value = !productData.isOpen;

		setProductData({
			...productData,
			[name]: value,
		});
	}

	// 상품 옵션 input 입력 이벤트
	const handleOptionOnChange = (e) => {
		let { name, value } = e.target;
		const idx = e.target.parentElement.parentElement.getAttribute('value');

		if(name === 'optionStock')
			value = Number(value);
			
		optionList[idx] = {
			...optionList[idx],
			[name]: value,
		};

		setOptionList([...optionList]);
	}

	// 상품 옵션 공개, 비공개 여부 radio 버튼 이벤트
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

    //옵션 추가 이벤트
    //동적으로 옵션 탭 Element 추가
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

	//옵션 제거 이벤트
    //동적으로 해당 옵션 Element 제거
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

	// 상품 수정 submit 이벤트
	const handleSubmitOnClick = async (e) => {
		e.preventDefault();

		try {
			const res = await patchProduct(
				productId,
				productData, 
				optionList, 
				newFirstThumbnail, 
				newThumbnail, 
				newInfoImage, 
				deleteFirstThumbnail, 
				deleteOption, 
				deleteThumbnail, 
				deleteInfoImage
			);

			navigate(`/admin/product/${res.data.id}`);
		} catch(err) {
			console.log(err);
		}
	}

	//상품 분류 select box 이벤트
    const handleSelectOnChange = (e) => {
        const value = e.target.value;

        setProductData({
            ...productData,
            classification: value,
        })
    }

    //대표 썸네일 input 이벤트
    const handleFirstThumbnailInputChange = (e) => {
        if(imageValidation(e)){
            const file = e.target.files[0];
            setNewFirstThumbnail(file);
        }
    }

    //새로 등록한 대표 썸네일 삭제 이벤트
    const handleRemoveFirstThumbnail = (e) => {
        window.URL.revokeObjectURL(newFirstThumbnail);
        setNewFirstThumbnail('');
    }

    //기존 대표 썸네일 삭제 이벤트
    const handleRemoveOriginalFirstThumbnail = (e) => {
        setDeleteFirstThumbnail(firstThumbnail);
        setFirstThumbnail('');
    }

    //썸네일 input 이벤트 ( multiple )
    const handleThumbnailInputChange = (e) => {
        const files = imageInputChange(e, newThumbnail);

        setNewThumbnail(files);
    }

    //새로 추가했던 상품 썸네일 제거 이벤트
    const handleRemoveThumbnail = (e) => {
        const deleteIdx = e.target.value;
        const files = [...newThumbnail];
        files.splice(deleteIdx, 1);
        setNewThumbnail(files);
    }

    //기존 상품 썸네일 제거 이벤트
    const handleRemoveOriginalThumbnail = (e) => {
        const deleteIdx = e.target.value;
        const files = [...thumbnail];
        const deleteFiles = [...deleteThumbnail];
        deleteFiles.push(files[deleteIdx]);
        files.splice(deleteIdx, 1);

        setDeleteThumbnail(deleteFiles);
        setThumbnail(files);
    }

    //상품 정보 이미지 input 이벤트 ( multiple )
    const handleInfoImageInputChange = (e) => {
        const files = imageInputChange(e, newInfoImage);

        setNewInfoImage(files);
        setInfoImageLength(infoImageLength + 1);
    }

    //새로 추가했던 상품 정보 이미지 제거 이벤트
    const handleRemoveInfoImage = (e) => {
        const deleteIdx = e.target.value;
        const files = [...newInfoImage];
        files.splice(deleteIdx, 1);
        setNewInfoImage(files);
        setInfoImageLength(infoImageLength - 1);
    }

    //기존 정보 이미지 제거 이벤트
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
            <AdminAddProductForm
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

export default AdminModifyProduct;