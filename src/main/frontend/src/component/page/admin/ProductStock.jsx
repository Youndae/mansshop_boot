import React, {useEffect, useState} from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";
import {useDispatch, useSelector} from "react-redux";
import {Link, useNavigate, useSearchParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import {getClickNumber, getNextNumber, getPrevNumber, productDetailPagingObject} from "../../../modules/pagingModule";
import {setMemberObject} from "../../../modules/loginModule";
import Paging from "../../ui/Paging";

/*
        상품 재고 관리 탭 컴포넌트

        테이블 구조로 처리하는데
        분류, 상품명, 공개여부가 상단
        하단에는 사이즈, 컬러, 재고, 공개여부로 처리한다.
        td 처리 함수 새로 생성하고 그 안에서 옵션 처리 function을 또 호출하도록 하면 되지 않을까?

        상품명 클릭 시 AdminProductDetail로 연결될 수 있도록 해 바로 수정이 가능하도록 처리한다.

        검색, 페이징

     */
function ProductStock() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');
    const keyword = params.get('keyword');
    const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [keywordInput, setKeywordInput] = useState('');

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        setKeywordInput(keyword);
        getProductStock();
    }, [page, keyword]);

    const getProductStock = async() => {

        await axiosInstance.get(`admin/product/stock?keyword=${keyword}&page=${page}`)
            .then(res => {
                console.log('stock res : ', res);

                setData(res.data.content);

                const pagingObject = productDetailPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: page,
                });

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('getProductStock Error : ', err);
            })
    }

    const handlePageBtn = (e) => {
        handlePagingSubmit(getClickNumber(e));
    }

    const handlePagePrev = () => {
        handlePagingSubmit(getPrevNumber(pagingData));
    }

    const handlePageNext = () => {
        handlePagingSubmit(getNextNumber(pagingData));
    }

    const handlePagingSubmit = (pageNum) => {
        if(keyword == null)
            navigate(`/admin/product/stock?page=${pageNum}`);
        else
            navigate(`/admin/product/stock?keyword=${keyword}&page=${pageNum}`);
    }

    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    const handleSearchOnClick = async () => {
        navigate(`/admin/product/stock?keyword=${keywordInput}`);
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
                    <Paging
                        pagingData={pagingData}
                        onClickNumber={handlePageBtn}
                        onClickPrev={handlePagePrev}
                        onClickNext={handlePageNext}
                        className={'like-paging'}
                    />
                </div>
            </div>
        </div>
    )
}

function StockBody(props) {
    const { data } = props;

    let isOpenText = '비공개';
    if(data.isOpen)
        isOpenText = '공개';

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
                <td>{isOpenText}</td>
            </tr>
            {data.optionList.map((optionData, index) => {
                const sizeText = `사이즈 : ${optionData.size}`;
                const colorText = `색상 : ${optionData.color}`;
                let optionText = '';
                if(optionData.size === null){
                    if(optionData.color !== null){
                        optionText = colorText;
                    }
                }else {
                    if(optionData.color !== null)
                        optionText = `${sizeText}, ${colorText}`;
                    else
                        optionText = sizeText;
                }
                let optionIsOpenText = '비공개';
                if(optionData.optionIsOpen)
                    optionIsOpenText = '공개';

                return (
                    <tr className="admin-stock-option">
                        <td>option</td>
                        <td>{optionText}</td>
                        <td>{optionData.optionStock}</td>
                        <td></td>
                        <td>{optionIsOpenText}</td>
                    </tr>
                )
            })}
        </>

    )
}

export default ProductStock;