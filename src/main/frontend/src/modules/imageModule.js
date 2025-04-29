//이미지 input시 파일 타입 검증 및 대기중인 파일 배열에 추가 후 배열 반환
export const imageInputChange = (e, files) => {
    const validationResult = imageValidation(e);

    if(validationResult){
        const fileList = e.target.files;
        let fileArr = [...files];

        for(let i = 0; i < fileList.length; i++)
            fileArr.push(fileList[i]);

        return fileArr;
    }else {
        return null;
    }
}

//이미지 타입 검증
export const imageValidation = (e) => {
    const files = e.target.files;

    for(let i = 0; i < files.length; i++) {
        const fileName = files[i].name;
        const fileNameExtensionIndex = fileName.lastIndexOf('.') + 1;
        const fileNameExtension = fileName.toLowerCase().substring(fileNameExtensionIndex, fileName.length);

        if(!(fileNameExtension === 'jpg') && !(fileNameExtension === 'gif')
            && !(fileNameExtension === 'png') && !(fileNameExtension === 'jpeg'))
            return false;
    }

    return true;
}

//formData 생성
export const setProductFormData = (productData, optionList, firstThumbnail, thumbnail, infoImage) => {
    let formData = new FormData();

    formData.append('classification', productData.classification);
    formData.append('productName', productData.productName);
    formData.append('price', productData.price);
    formData.append('isOpen', productData.isOpen);
    formData.append('discount', productData.discount);
    for(let i = 0; i < optionList.length; i++) {
        formData.append('optionList[' + i + '].optionId', optionList[i].optionId);
        formData.append('optionList[' + i + '].size', optionList[i].size);
        formData.append('optionList[' + i + '].color', optionList[i].color);
        formData.append('optionList[' + i + '].optionStock', optionList[i].optionStock);
        formData.append('optionList[' + i + '].optionIsOpen', optionList[i].optionIsOpen);
    }

    thumbnail.forEach(file => formData.append('thumbnail', file));
    infoImage.forEach(file => formData.append('infoImage', file));

    if(firstThumbnail !== '')
        formData.append('firstThumbnail', firstThumbnail);

    return formData;

}