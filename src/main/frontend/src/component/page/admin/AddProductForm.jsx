import React, {useEffect, useState} from 'react';

import DefaultBtn from "../../ui/DefaultBtn";
import Image from "../../ui/Image";

function AddProductForm(props) {
    const { productData
        , optionList
        , headerText
        , handleProductOnChange
        , handleAddOption
        , handleRemoveOption
        , handleOptionOnChange
        , submitBtnText
        , handleSubmitOnClick
        , handleOptionRadioOnChange
        , firstThumbnail
        , newFirstThumbnail
        , thumbnail
        , newThumbnail
        , infoImage
        , newInfoImage
        , infoImageLength
        , classification
        , handleSelectOnChange
        , handleFirstThumbnailInputChange
        , handleRemoveFirstThumbnail
        , handleRemoveOriginalFirstThumbnail
        , handleThumbnailInputChange
        , handleRemoveThumbnail
        , handleRemoveOriginalThumbnail
        , handleInfoImageInputChange
        , handleRemoveInfoImage
        , handleRemoveOriginalInfoImage
    } = props;

    return (
        <div className="admin-content">
            <div className="admin-content-header">
                <h1>{headerText}</h1>
            </div>
            <div className="admin-content-content">
                <DefaultBtn
                    btnText={submitBtnText}
                    onClick={handleSubmitOnClick}
                    className={'product-submit-btn'}
                />
                <div className="product-name add-product">
                    <label className="product-label">상품명</label>
                    <input className="product-input" type={'text'} name={'productName'} onChange={handleProductOnChange} value={productData.productName}/>
                </div>
                <div className="product-classification add-product">
                    <label className="product-label">상품 분류</label>
                    <ProductClassification
                        data={classification}
                        value={productData.classification}
                        handleSelectOnChange={handleSelectOnChange}
                    />
                </div>
                <div className="product-price add-product">
                    <label className="product-label">가격</label>
                    <input className="product-input" type={'text'} name={'price'} onChange={handleProductOnChange} value={productData.price}/>
                </div>
                <div className="product-isOpen add-product">
                    <label className="product-label">공개여부</label>
                    <div className="product-isOpen-radio isOpen-radio">
                        <label className="radio-label">공개</label>
                        <input className="radio-input" type={'radio'} name={'isOpen'} onChange={handleProductOnChange} checked={productData.isOpen}/>
                        <label className="radio-label">비공개</label>
                        <input className="radio-input" type={'radio'} name={'isOpen'} onChange={handleProductOnChange} checked={!productData.isOpen}/>
                    </div>
                </div>
                <div className="product-discount add-product">
                    <label className="product-label">할인율(%)</label>
                    <input type='number' className="product-input" name={'discount'} onChange={handleProductOnChange} value={productData.discount}/>
                </div>
                <div className="option-test">
                    <div className="option-header">
                        <h3>상품 옵션</h3>
                        <DefaultBtn
                            btnText={'옵션 추가'}
                            onClick={handleAddOption}
                        />
                    </div>
                    {optionList.map((data, index) => {
                        let sizeText = '';
                        let colorText = '';
                        if(data.size !== null)
                            sizeText = data.size;
                        if(data.color !== null)
                            colorText = data.color;
                        return (
                            <div key={index} value={index} className="option-detail">
                                <div className="option-detail-header">
                                    <DefaultBtn
                                        btnText={'옵션 삭제'}
                                        onClick={handleRemoveOption}
                                        name={data.optionId}
                                        value={index}
                                    />
                                </div>
                                <div className="option-size">
                                    <label className="product-label">사이즈</label>
                                    <input className="product-input" type={'text'} name={'size'} onChange={handleOptionOnChange} value={sizeText}/>
                                </div>
                                <div className="option-color">
                                    <label className="product-label">컬러</label>
                                    <input className="product-input" type={'text'} name={'color'} onChange={handleOptionOnChange} value={colorText}/>
                                </div>
                                <div className="option-stock">
                                    <label className="product-label">재고</label>
                                    <input className="product-input" type={'number'} name={'optionStock'} onChange={handleOptionOnChange} value={data.optionStock}/>
                                </div>
                                <div className="option-isOpen">
                                    <label className="product-label">옵션 공개여부</label>
                                    <div className="product-isOpen-radio isOpen-radio">
                                        <label className="radio-label-label">공개</label>
                                        <input className="radio-input" type={'radio'} name={`optionIsOpen/${index}`} onChange={handleOptionRadioOnChange} checked={data.optionIsOpen}/>
                                        <label className="radio-label">비공개</label>
                                        <input className="radio-input" type={'radio'} name={`optionIsOpen/${index}`} onChange={handleOptionRadioOnChange} checked={!data.optionIsOpen}/>
                                    </div>
                                </div>
                            </div>
                        )
                    })}
                </div>
                <div className="add-product-thumbnail">
                    <div className="add-product-first-thumbnail">
                        <div className="first-thumbnail-header thumbnail-header">
                            <h3>대표 썸네일</h3>
                        </div>
                        <div className="first-thumbnail-input thumbnail-input">
                            <label htmlFor="first-thumb">
                                <div className="file-input-btn">
                                    대표 썸네일 업로드
                                </div>
                            </label>
                            <input type='file' id='first-thumb' name='first-thumb' className={'file-input'} onClick={(e) => {e.target.value = null}} onChange={handleFirstThumbnailInputChange}/>
                        </div>
                        <div className="first-thumbnail-content thumbnail-content">
                            <FirstThumbnailPreview
                                firstThumbnail={firstThumbnail}
                                newFirstThumbnail={newFirstThumbnail}
                                handleRemoveFirstThumbnail={handleRemoveFirstThumbnail}
                                handleRemoveOriginalFirstThumbnail={handleRemoveOriginalFirstThumbnail}
                            />
                        </div>
                    </div>
                    <div className="add-product-thumbnail">
                        <div className="first-thumbnail-header thumbnail-header">
                            <h3>썸네일</h3>
                        </div>
                        <div className="product-thumbnail-input thumbnail-input">
                            <label htmlFor="product-thumb">
                                <div className="file-input-btn">
                                    썸네일 업로드
                                </div>
                            </label>
                            <input type='file' id='product-thumb' className={'file-input'} onChange={handleThumbnailInputChange} onClick={(e) => {e.target.value = null}} multiple/>
                        </div>
                        <div className="product-thumbnail-content thumbnail-content">
                            {thumbnail.map((imageName, index) => {
                                return (
                                    <div key={index}>
                                        <button className="thumbnail-delete-btn image-btn" type={'button'} value={index} onClick={handleRemoveOriginalThumbnail}>
                                            삭제
                                        </button>
                                        <Image
                                            key={index}
                                            imageName={imageName}
                                        />
                                    </div>
                                )
                            })}
                            {newThumbnail.map((file, index) => {
                                return (
                                    <div key={index}>
                                        <button className="thumbnail-delete-btn image-btn" type={'button'} value={index} onClick={handleRemoveThumbnail}>
                                            삭제
                                        </button>
                                        <PreviewImage
                                            key={index}
                                            file={file}
                                        />
                                    </div>
                                )
                            })}
                        </div>
                    </div>
                    <div className="add-product-info-image">
                        <div className="info-image-header thumbnail-header">
                            <h3>상품 정보 이미지({infoImageLength})</h3>
                        </div>
                        <div className="info-image-input thumbnail-input">
                            <label htmlFor="info-image">
                                <div className="file-input-btn">
                                    상품 정보 업로드
                                </div>
                            </label>
                            <input type='file' id='info-image' className={'file-input'} onChange={handleInfoImageInputChange} onClick={(e) => {e.target.value = null}} multiple/>
                        </div>
                        <div className="info-image-content thumbnail-content">
                            {infoImage.map((imageName, index) => {
                                return (
                                    <div key={index} className="info-image-content-image">
                                        <button className="info-image-delete-btn image-btn" type={'button'} value={index} onClick={handleRemoveOriginalInfoImage}>
                                            삭제
                                        </button>
                                        <Image
                                            key={index}
                                            imageName={imageName}
                                        />
                                    </div>
                                )
                            })}
                            {newInfoImage.map((file, index) => {
                                return (
                                    <div key={index} className="info-image-content-image">
                                        <button className="info-image-delete-btn image-btn" type={'button'} value={index} onClick={handleRemoveInfoImage}>
                                            삭제
                                        </button>
                                        <PreviewImage
                                            key={index}
                                            file={file}
                                        />
                                    </div>
                                )
                            })}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

function ProductClassification(props) {
    const { data, value, handleSelectOnChange } = props;

    return (
        <select className="product-classification-select-box product-input" value={value} onChange={handleSelectOnChange}>
            <option value={'default'} disabled={true}>상품 분류를 선택해주세요</option>
            {data.map((option, index) => {
                return (
                    <option key={index} value={option}>{option}</option>
                )
            })}
        </select>
    )
}

function FirstThumbnailPreview(props) {
    const { firstThumbnail, newFirstThumbnail, handleRemoveFirstThumbnail, handleRemoveOriginalFirstThumbnail } = props;

    if(firstThumbnail.length !== 0){
        return (
            <>
                <button className="first-thumbnail-delete-btn image-btn" type={'button'} onClick={handleRemoveOriginalFirstThumbnail}>
                    삭제
                </button>
                <Image
                    imageName={firstThumbnail}
                />
            </>
        )
    }else if(newFirstThumbnail !== '') {
        return (
            <>
                <button className="first-thumbnail-delete-btn image-btn" type={'button'} onClick={handleRemoveFirstThumbnail}>
                    삭제
                </button>
                <PreviewImage
                    file={newFirstThumbnail}
                />
            </>
        )
    }
}

function PreviewImage(props) {
    const { file } = props;

    const url = window.URL.createObjectURL(file);

    return (
        <img src={url} alt={''} />
    )
}

export default AddProductForm;