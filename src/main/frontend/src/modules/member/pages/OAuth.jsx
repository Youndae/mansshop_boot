import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { tokenRequest } from '../services/memberService';

function OAuth() {
    const navigate = useNavigate();

	useEffect(() => {
		const issuedToken = async () => {
			try{
				const res = await tokenRequest();
				const authorization = res.data.authorization;
				window.localStorage.setItem('authorization', authorization);
				
				const prevUrl = window.sessionStorage.getItem('prev');
				navigate(prevUrl);
			}catch(err){
				console.log(err);
			}
		}

		issuedToken();
	}, []);

	return null;
}

export default OAuth;