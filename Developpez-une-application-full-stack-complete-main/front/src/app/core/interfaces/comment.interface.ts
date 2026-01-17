export interface PostComment {
  id?: number;
  contenu: string;
  authorName?: string;
  authorId?: number;
  postId?: number;
  createdAt?: string;
}