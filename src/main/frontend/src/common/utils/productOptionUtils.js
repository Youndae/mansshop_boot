// 상품 옵션 텍스트 생성
export const getProductOption = ({size, color}) => {
	if(size && color)
		return `사이즈: ${size} 컬러: ${color}`;
	if(size)
		return `사이즈: ${size}`;
	if(color)
		return `컬러: ${color}`;

	return '';
}