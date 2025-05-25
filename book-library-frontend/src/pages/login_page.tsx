import React, { useState } from 'react';
import axios from 'axios';
import {useRouter} from "next/router";


export default function LoginPage() {
    type User = {
        id: number;
        name: string;
        email: string;
    };
    const router = useRouter();
    const [result, setResult] = useState<User | User[] | null>(null);
    const [name, setName] = useState('');
    const [showSearchResult, setShowSearchResult] = useState(false);

    const handleSearch = async () => {
        const res = await axios.get(`http://localhost:8081/getuser`, {params: {name}});
        setResult(res.data);
        setShowSearchResult(true);
    };
    const renderResult = () => {
        if (!result) return null;

        if (Array.isArray(result)) {
            return result.map((user) => (
                <div key={user.id}>
                    <h2>{user.name}</h2>
                    <p>{user.email}</p>
                </div>
            ));
        } else {
            return (
                <div>
                    <h2>{result.name}</h2>
                    <p>{result.email}</p>
                </div>
            );
        }
    };

    return (
        <div>
            <input value={name} onChange={e => setName(e.target.value)} />
            <button onClick={handleSearch}>log-in</button>
            <button onClick={() => router.push('/page')}>back to home page</button>
            {showSearchResult && renderResult()}
        </div>
    )
}