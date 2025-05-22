import React, { useEffect, useState } from "react";

import { getNewProductList } from "../services/mainService";

import MainContent from "../components/MainContent";

/*
    새로운 상품 리스트.
    상품 등록일을 기준으로 조회
 */
function New() {
	const [data, setData] = useState([]);

	useEffect(() => {
		const getNew = async () => {
			try {
				const res = await getNewProductList();
				setData(res.data);
			} catch (err) {
				console.log(err);
			}
		}

		getNew();
	}, []);

	return (
		<>
			<MainContent
				data={data}
				classification={'NEW'}
			/>
		</>
	)
}

export default New;