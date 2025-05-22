import React from 'react';
import { useEffect } from "react";
import { useDispatch } from 'react-redux';
import { login, logout } from './modules/member/memberSlice';
import { getMemberStatus } from "./common/services/authService";
import { getToken } from './common/utils/axios/tokenUtils';
import {
    BrowserRouter,
    Routes,
    Route
} from "react-router-dom";



// Navbar
import Navbar from './common/components/Navbar';

// Main
import Best from './modules/main/pages/Best';
import New from './modules/main/pages/New';
import MainClassification from './modules/main/pages/MainClassification';
import SearchProduct from './modules/main/pages/SearchProduct';

// Anonymous order
import AnonymousOrderInfo from './modules/main/pages/AnonymousOrderInfo';
import AnonymousOrderList from './modules/main/pages/AnonymousOrderList';

//product
import ProductDetail from './modules/product/pages/ProductDetail';

// cart
import Cart from './modules/cart/pages/Cart';

// order
import Order from './modules/order/pages/Order';

//member
import Login from './modules/member/pages/Login';
import Register from './modules/member/pages/Register';
import OAuth from './modules/member/pages/OAuth';
import SearchId from './modules/member/pages/SearchId';
import SearchPw from './modules/member/pages/SearchPw';
import ResetPassword from './modules/member/pages/ResetPassword';

// mypage
import MyPageOrder from './modules/mypage/pages/order/MyPageOrder';
import MyPageLikeProduct from './modules/mypage/pages/likeProduct/MyPageLikeProduct';
import MyPageProductQnA from './modules/mypage/pages/qna/MyPageProductQnA';
import MyPageProductQnADetail from './modules/mypage/pages/qna/MyPageProductQnADetail';
import MyPageMemberQnA from './modules/mypage/pages/qna/MyPageMemberQnA';
import MyPageMemberQnADetail from './modules/mypage/pages/qna/MyPageMemberQnADetail';
import MyPageMemberQnAWrite from './modules/mypage/pages/qna/MyPageMemberQnAWrite';
import MyPageMemberQnAModify from './modules/mypage/pages/qna/MyPageMemberQnAModify';
import MyPageReview from './modules/mypage/pages/review/MyPageReview';
import MyPageReviewWrite from './modules/mypage/pages/review/MyPageReviewWrite';
import MyPageReviewModify from './modules/mypage/pages/review/MyPageReviewModify';
import MyPageUpdateInfo from './modules/mypage/pages/info/MyPageUpdateInfo';

// admin
import AdminProductList from './modules/admin/pages/product/AdminProductList';
import AdminProductDetail from './modules/admin/pages/product/AdminProductDetail';
import AdminAddProduct from './modules/admin/pages/product/AdminAddProduct';
import AdminModifyProduct from './modules/admin/pages/product/AdminModifyProduct';
import AdminProductStock from './modules/admin/pages/product/AdminProductStock';
import AdminDiscountProductList from './modules/admin/pages/product/AdminDiscountProductList';
import AdminProductDiscount from './modules/admin/pages/product/AdminProductDiscount';
import AdminNewOrderList from './modules/admin/pages/order/AdminNewOrderList';
import AdminAllOrderList from './modules/admin/pages/order/AdminAllOrderList';
import AdminProductQnAList from './modules/admin/pages/qna/AdminProductQnAList';
import AdminProductQnADetail from './modules/admin/pages/qna/AdminProductQnADetail';
import AdminMemberQnAList from './modules/admin/pages/qna/AdminMemberQnAList';
import AdminMemberQnADetail from './modules/admin/pages/qna/AdminMemberQnADetail';
import AdminQnAClassification from './modules/admin/pages/qna/AdminQnAClassification';
import AdminReviewList from './modules/admin/pages/review/AdminReviewList';
import AdminReviewDetail from './modules/admin/pages/review/AdminReviewDetail';
import AdminMemberList from './modules/admin/pages/member/AdminMemberList';
import AdminPeriodSales from './modules/admin/pages/sales/AdminPeriodSales';
import AdminPeriodSalesDetail from './modules/admin/pages/sales/AdminPeriodSalesDetail';
import AdminPeriodSalesDailyOrderList from './modules/admin/pages/sales/AdminPeriodSalesDailyOrderList';
import AdminProductSales from './modules/admin/pages/sales/AdminProductSales';
import AdminProductSalesDetail from './modules/admin/pages/sales/AdminProductSalesDetail';
import AdminFailedQueueList from './modules/admin/pages/queue/AdminFailedQueueList';

// error
import Error from './modules/error/Error';


function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    const accessToken = getToken();

    if(!accessToken) {
      dispatch(logout());
      return;
    }

    getMemberStatus()
		.then(({ userId, role}) => {
			dispatch(login({ userId, role }));
		})
		.catch((err) => {
			console.error('회원 상태 확인 실패: ', err);
			dispatch(logout());
		})
  }, [dispatch]);

  return (
	<BrowserRouter>
		<div className='container'>
			<Navbar />
			<Routes>
				<Route index element={<Best />}/>
				<Route path='new' element={<New />} />
				<Route path='category/:classification' element={<MainClassification />} />
				<Route path="search" element={<SearchProduct />} />

				<Route path='order/info' element={<AnonymousOrderInfo />}/>
				<Route path='order/detail' element={<AnonymousOrderList />}/>

				<Route path='product/:productId' element={<ProductDetail />}/>
				
				<Route path='cart' element={<Cart />}/>

				<Route path='payment' element={<Order />}/>

				<Route path='login' element={<Login />} />
              	<Route path='register' element={<Register />} />
				<Route path="oAuth" element={<OAuth />} />
				<Route path='search-id' element={<SearchId />}/>
				<Route path='search-pw' element={<SearchPw />}/>
				<Route path='reset-pw' element={<ResetPassword />} />

				<Route path='my-page/order' element={<MyPageOrder />}/>
				<Route path='my-page/like' element={<MyPageLikeProduct />}/>
				<Route path='my-page/qna/product' element={<MyPageProductQnA />} />
				<Route path='my-page/qna/product/detail/:qnaId' element={<MyPageProductQnADetail />}/>
				<Route path='my-page/qna/member' element={<MyPageMemberQnA />} />
				<Route path='my-page/qna/member/detail/:qnaId' element={<MyPageMemberQnADetail />}/>
				<Route path='my-page/qna/member/write' element={<MyPageMemberQnAWrite />}/>
				<Route path='my-page/qna/member/update/:qnaId' element={<MyPageMemberQnAModify />}/>
				<Route path='my-page/review' element={<MyPageReview />} />
				<Route path='my-page/review/write' element={<MyPageReviewWrite />} />
				<Route path='my-page/review/modify/:reviewId' element={<MyPageReviewModify />} />
				<Route path='my-page/info' element={<MyPageUpdateInfo />} />


				<Route path='admin/product' element={<AdminProductList />} />
				<Route path='admin/product/:productId' element={<AdminProductDetail />}/>
				<Route path='admin/product/add' element={<AdminAddProduct />}/>
				<Route path='admin/product/update/:productId' element={<AdminModifyProduct />}/>
				<Route path='admin/product/stock' element={<AdminProductStock />}/>
				<Route path='admin/product/discount' element={<AdminDiscountProductList />}/>
				<Route path='admin/product/discount/setting' element={<AdminProductDiscount />}/>

				<Route path='admin/order' element={<AdminNewOrderList />} />
				<Route path='admin/order/all' element={<AdminAllOrderList />} />

				<Route path='admin/qna/product' element={<AdminProductQnAList />} />
				<Route path='admin/qna/product/:qnaId' element={<AdminProductQnADetail />}/>
				<Route path='admin/qna/member' element={<AdminMemberQnAList />} />
				<Route path='admin/qna/member/:qnaId' element={<AdminMemberQnADetail />}/>
				<Route path='admin/qna/classification' element={<AdminQnAClassification />} />

				<Route path='admin/review' element={<AdminReviewList />}/>
				<Route path='admin/review/all' element={<AdminReviewList />}/>
				<Route path='admin/review/detail/:reviewId' element={<AdminReviewDetail />}/>

				<Route path='admin/member' element={<AdminMemberList />}/>
				
				<Route path='admin/sales/period' element={<AdminPeriodSales />}/>
				<Route path='admin/sales/period/:date' element={<AdminPeriodSalesDetail />} />
				<Route path='admin/sales/period/detail/daily/:selectDate' element={<AdminPeriodSalesDailyOrderList />} />
				<Route path='admin/sales/product' element={<AdminProductSales />} />
				<Route path='admin/sales/product/:productId' element={<AdminProductSalesDetail />} />
				
				<Route path='admin/failedQueue' element={<AdminFailedQueueList />} />

				<Route path='error' element={<Error />} />
				<Route path='/*' element={<Error />} />
			</Routes>
		</div>
	</BrowserRouter>
  )
}
export default App;
