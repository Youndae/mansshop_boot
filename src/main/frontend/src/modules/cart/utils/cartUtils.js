export const setSelectCartDetail = (selectValue) => {
	let arr = [...selectValue];
	let resultArr = [];
	for(let i = 0; i < arr.length; i++) {
		if(arr[i].status)
			resultArr.push(Number(arr[i].cartDetailId));
	}

	return resultArr;
}

export const setAllCartDetail = (selectValue) => {
	let arr = [...selectValue];
	let resultArr = [];

	for(let i = 0; i < arr.length; i++)
		resultArr.push(arr[i].cartDetailId);

	return resultArr;
}

export const setCheckBoxStatus = ({idx, selectValue}) => {
	const arr = [...selectValue];
	arr[idx] = {
		...arr[idx],
		status: !arr[idx].status
	}

	return arr;
}