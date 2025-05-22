import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';

import { getProductStockList } from '../../services/adminProductService';
import { mainProductPagingObject } from '../../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../../common/utils/paginationUtils';
import { buildQueryString } from '../../../../common/utils/queryStringUtils';
import { getProductOption } from '../../../../common/utils/productOptionUtils';

import AdminSideNav from '../../components/AdminSideNav';
import Pagination from '../../../../common/components/Pagination';

/*
    상품 재고 관리 페이지
    테이블 구조.
    상품 elements 클릭 시 상품 정보로 이동
    상품 elements 하위에
    상품 옵션별 elements를 통해 옵션별 재고 현황 확인 가능.

    검색은 상품명 기반.
*/
function AdminProductStock() {
	const [ params ] = useSearchParams();
	const { page, keyword } = Object.fromEntries(params);

	const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [keywordInput, setKeywordInput] = useState('');

    const navigate = useNavigate();

	useEffect(() => {
		const getProductStock = async() => {
			try {
				const res = await getProductStockList(page, keyword);

				setData(res.data.content);

                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
				});
			} catch(err) {
				console.log(err);
			}
		}

		getProductStock();
	}, [page, keyword]);
	
	//페이지네이션 버튼 이벤트
	const handlePageBtn = (type) => {
		handlePageChange({
			typeOrNumber: type,
			pagingData,
			navigate,
			keyword: keyword,
		});
	}

	//검색 input 입력 이벤트
    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

	//검색 버튼 이벤트
	const handleSearchOnClick = async () => {
		const queryString = buildQueryString({keyword: keywordInput});
		navigate(`${queryString}`);
	}

	return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'product'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>상품 재고</h1>
                </div>
                <div className="admin-content-content">
                    <table className="admin-content-table product-stock-table">
                        <thead>
                            <tr>
                                <th>분류</th>
                                <th>상품명</th>
                                <th>총 재고</th>
                                <th>옵션 수</th>
                                <th>공개 여부</th>
                            </tr>
                        </thead>
                        <tbody>
                        {data.map((data, index) => {
                            return (
                                <StockBody
                                    key={index}
                                    data={data}
                                />
                            )
                        })}
                        </tbody>
                    </table>
                </div>
                <div className="admin-search">
                    <input type={'text'} onChange={handleKeywordOnChange} value={keywordInput}/>
                    <img alt={''} src={"https://as1.ftcdn.net/v2/jpg/03/25/73/68/1000_F_325736897_lyouuiCkWI59SZAPGPLZ5OWQjw2Gw4qY.jpg"} onClick={handleSearchOnClick}/>
                    <Pagination
                        pagingData={pagingData}
                        handlePageBtn={handlePageBtn}
                        className={'like-paging'}
                    />
                </div>
            </div>
        </div>
    )
}

function StockBody(props) {
    const { data } = props;

	const isOpenText = (isOpen) => isOpen ? '공개' : '비공개';

    return (
        <>
            <tr>
                <td>{data.classification}</td>
                <td>
                    <Link to={`/admin/product/${data.productId}`}>
                        {data.productName}
                    </Link>
                </td>
                <td>{data.totalStock}</td>
                <td>{data.optionList.length}</td>
                <td>{isOpenText(data.isOpen)}</td>
            </tr>
            {data.optionList.map((optionData, index) => {
				const productOptionText = getProductOption(optionData);

                return (
                    <tr className="admin-stock-option" key={index}>
                        <td colSpan={2}>{productOptionText}</td>
                        <td>{optionData.optionStock}</td>
                        <td></td>
                        <td>{isOpenText(optionData.optionIsOpen)}</td>
                    </tr>
                )
            })}
        </>

    )
}

export default AdminProductStock;