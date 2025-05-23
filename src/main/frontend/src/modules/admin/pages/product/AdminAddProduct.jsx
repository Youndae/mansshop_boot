import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { 
	getProductClassificationList,
	postProduct
} from '../../services/adminProductService';
import { imageInputChange, imageValidation } from '../../../../common/utils/ImageUtils';

import AdminSideNav from '../../components/AdminSideNav';
import AdminAddProductForm from '../../components/AdminAddProductForm';

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
function AdminAddProduct() {
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

	useEffect(() => {
		const getClassification = async () => {
			try {
				const res = await getProductClassificationList();

				setClassification(res.data);
			} catch(err) {
				console.log(err);
			}
		}

		getClassification();
	}, []);

	// 상품 데이터 input 입력 이벤트
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

	//상품 옵션 input 입력 이벤트
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

	//상품 옵션 공개, 비공개 설정 Radio 이벤트
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

	// 상품 옵션 추가 버튼 이벤트
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

	//상품 옵션 제거 버튼 이벤트
    const handleRemoveOption = (e) => {
        const idx = e.target.value;

        const optionArr = [...optionList];

        optionArr.splice(idx, 1);

        setOptionList(optionArr);
    }

	// 상품 추가 submit 이벤트
	const handleSubmitOnClick = async () => {
		try {
			const res = await postProduct(
				productData, 
				optionList, 
				newFirstThumbnail, 
				newThumbnail, 
				newInfoImage
			);

			navigate(`/admin/product/${res.data.id}`);
		} catch(err) {
			console.log(err);
		}
	}
	
	//상품 분류 select box 선택 이벤트
    const handleSelectOnChange = (e) => {
        setProductData({
            ...productData,
            classification: e.target.value
        });
    }

    //대표 썸네일 input 이벤트
    const handleFirstThumbnailInputChange = (e) => {
        if(imageValidation(e)){
            const file = e.target.files[0];
            setNewFirstThumbnail(file);
        }
    }

    //대표 썸네일 제거 이벤트
    const handleRemoveFirstThumbnail = (e) => {
        window.URL.revokeObjectURL(newFirstThumbnail);
        setNewFirstThumbnail('');
    }

	//상품 썸네일 input 이벤트
    const handleThumbnailInputChange = (e) => {
        const files = imageInputChange(e, newThumbnail);

        setNewThumbnail(files);
    }

    //상품 썸네일 제거 이벤트
    const handleRemoveThumbnail = (e) => {
        const deleteIdx = e.target.value;
        const files = [...newThumbnail];

        files.splice(deleteIdx, 1);

        setNewThumbnail(files);
    }

    //상품 정보 이미지 input 이벤트
    const handleInfoImageInputChange = (e) => {
        const files = imageInputChange(e, newInfoImage);

        setNewInfoImage(files);
        setInfoImageLength(files.length);
    }

    //상품 정보 이미지 제거 이벤트
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
            <AdminAddProductForm
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

export default AdminAddProduct;