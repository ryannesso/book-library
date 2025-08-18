import { GetServerSideProps } from "next";
import axios from "axios";
import { useState } from "react";
import { useRouter } from "next/router";

type Book = {
    id: number;
    title: string;
    author: string;
    description: string;
    price: number;
    availableCopies: number;
};

type Comment = {
    id: number;
    text: string;
    username: string;
    createdAt: string;
};

type Props = {
    book: Book | null;
    comments: Comment[];
};

export default function BookPage({ book, comments: initialComments }: Props) {
    const router = useRouter();
    const [comments, setComments] = useState<Comment[]>(initialComments);
    const [newComment, setNewComment] = useState("");
    const [error, setError] = useState<string | null>(null);

    if (!book) {
        return <p className="p-10 text-center text-lg">Book not found</p>;
    }

    const API_URL = typeof window === "undefined"
        ? process.env.API_URL_DOCKER || "http://backend:8081"
        : process.env.NEXT_PUBLIC_API_URL || "http://localhost:8081";

    const handleAddComment = async () => {
        if (!newComment.trim()) return;

        try {
            const res = await axios.post(
                `${API_URL}/api/comments/${book.id}`,
                { text: newComment },
                { withCredentials: true }
            );
            setComments([...comments, res.data]);
            setNewComment("");
            setError(null);
        } catch (err: any) {
            console.error("Error adding comment:", err);
            setError("Failed to add comment. Make sure you are logged in.");
        }
    };

    return (
        <div className="p-10 max-w-3xl mx-auto">
            <button
                onClick={() => router.push("/page")}
                className="mb-6 text-blue-600 hover:underline"
            >
                ← Back to Home
            </button>

            <h1 className="text-3xl font-bold">{book.title}</h1>
            <p className="text-lg text-gray-600">Author: {book.author}</p>
            <p className="mt-4">{book.description}</p>
            <p className="mt-4 font-semibold">Price: ${book.price}</p>
            <p>Available copies: {book.availableCopies}</p>

            <div className="mt-10">
                <h2 className="text-2xl font-semibold mb-4">Comments</h2>

                {comments.length === 0 ? (
                    <p className="text-gray-500">No comments yet.</p>
                ) : (
                    <ul>
                        {comments.map((c) => (
                            <li key={c.id} className="border-b py-2">
                                <p className="text-white">{c.text}</p>
                                <span className="text-sm text-gray-500">
                                    by {c.username} at {new Date(c.createdAt).toLocaleString()}
                                </span>
                            </li>
                        ))}
                    </ul>
                )}

                <div className="mt-6">
                    <textarea
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                        placeholder="Write a comment..."
                        className="w-full border p-2 rounded mb-2"
                    />
                    <button
                        onClick={handleAddComment}
                        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        Add Comment
                    </button>
                    {error && <p className="text-red-500 mt-2">{error}</p>}
                </div>
            </div>
        </div>
    );
}

export const getServerSideProps: GetServerSideProps = async (context) => {
    const { id } = context.params!;
    const API_URL = process.env.API_URL_DOCKER || "http://backend:8081";

    // ручной парсинг cookie
    const rawCookie = context.req.headers.cookie || "";
    const cookies: Record<string, string> = {};
    rawCookie.split(";").forEach((c) => {
        const [key, ...rest] = c.trim().split("=");
        if (key) cookies[key] = decodeURIComponent(rest.join("="));
    });

    const token = cookies["jwt"];

    if (!token) {
        return {
            redirect: {
                destination: "/login",
                permanent: false,
            },
        };
    }

    try {
        const headers = { Authorization: `Bearer ${token}` };

        const [bookRes, commentsRes] = await Promise.all([
            axios.get(`${API_URL}/api/books/${id}`, { headers }),
            axios.get(`${API_URL}/api/comments/${id}`, { headers }),
        ]);

        return {
            props: {
                book: bookRes.data,
                comments: commentsRes.data,
            },
        };
    } catch (error) {
        console.error("Error fetching book data:", error);
        return {
            props: {
                book: null,
                comments: [],
            },
        };
    }
};
