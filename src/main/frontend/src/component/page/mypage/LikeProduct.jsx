import React, {useEffect, useState} from 'react';
import {Link, useNavigate, useSearchParams} from "react-router-dom";

import {axiosDefault, axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber, mainProductPagingObject,
    pageSubmit
} from "../../../modules/pagingModule";
import {numberComma} from "../../../modules/numberCommaModule";
import {createPageParam} from "../../../modules/requestUrlModule";

import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import Paging from "../../ui/Paging";
import Image from "../../ui/Image";


// 관심상품 목록 페이지
function LikeProduct() {
    const [params] = useSearchParams();
    const page = params.get('page');

    const [likeData, setLikeData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });

    const navigate = useNavigate();

    useEffect(() => {
        getLikeProduct();
    }, [page]);

    //관심상품 목록 조회
    const getLikeProduct = async() => {

        await axiosInstance.get(`my-page/like${createPageParam(page)}`)
            .then(res => {
                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
                });

                setLikeData(res.data.content);
            })
    }

    //페이지네이션 버튼 이벤트
    const handlePageBtn = (e) => {
        pageSubmit(getClickNumber(e), navigate);
    }

    //페이지네이션 이전 버튼 이벤트
    const handlePagePrev = () => {
        pageSubmit(getPrevNumber(pagingData), navigate);
    }

    //페이지네이션 다음 버튼 이벤트
    const handlePageNext = () => {
        pageSubmit(getNextNumber(pagingData));
    }

    //관심상품 제거 요청
    const handleRemoveProductLike = async (e) => {
        const productId = e.target.name;

        await axiosDefault.delete(`product/de-like/${productId}`)
            .then(res => {
                if(checkResponseMessageOk(res)) {
                    getLikeProduct();
                }
            })
            .catch(() => {
                alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
            })
    }

    return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={false}
            />
            <div className="mypage-content test-div">
                <div className="mypage-like-header">
                    <h1>관심 상품</h1>
                </div>
                <div className="mypage-like-content">
                    <div className="mypage-like-content-list">
                        {likeData.map((data, index) => {
                            return (
                                <div key={index} className="mypage-like-detail">
                                    <LikeListContent
                                        data={data}
                                        handleRemoveProductLike={handleRemoveProductLike}
                                    />
                                </div>
                            )
                        })}
                    </div>
                </div>
                <Paging
                    pagingData={pagingData}
                    onClickNumber={handlePageBtn}
                    onClickPrev={handlePagePrev}
                    onClickNext={handlePageNext}
                    className={'like-paging'}
                />
            </div>
        </div>
    )
}

function LikeListContent(props) {
    const { data, handleRemoveProductLike } = props;

    return (
        <div className="mypage-like-data-detail">
            <div className="mypage-like-detail-content">
                <div className="mypage-like-remove">
                    <img className="mypage-like-delete-btn" src={`${process.env.PUBLIC_URL}/image/del.jpg`} name={data.productId}
                         onClick={handleRemoveProductLike} alt={''}/>
                </div>
                <div className="mypage-like-thumb">
                    <Image imageName={data.thumbnail} className={'mypage-like-thumbnail'}/>
                    <div className="mypage-like-info">
                        <Link to={`/product/${data.productId}`}>
                            <span className="like-data-product-name">{data.productName}</span>
                        </Link>
                        <span className="like-data-product-price">{numberComma(data.productPrice)} 원</span>
                    </div>
                </div>

            </div>
        </div>
    )

}

export default LikeProduct;