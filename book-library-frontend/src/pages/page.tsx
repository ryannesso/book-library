import {useRouter} from "next/router";
export default function Page() {
    const router = useRouter();
    return (
        <div>
            <h1>
                home page
            </h1>
            <button onClick={() => router.push('/login_page')}>log-in</button>
            <button onClick={() => router.push('/sign_page')}>sign-in</button>
        </div>
    );

}