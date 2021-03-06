package com.tejatummalapalli.sparkblog.dao;

import com.github.slugify.Slugify;
import com.tejatummalapalli.sparkblog.Exceptions.BlogNotFoundException;
import com.tejatummalapalli.sparkblog.Exceptions.BlogNotValidException;
import com.tejatummalapalli.sparkblog.Exceptions.CommentNotValidException;
import com.tejatummalapalli.sparkblog.model.Blog;
import com.tejatummalapalli.sparkblog.model.BlogEntry;
import com.tejatummalapalli.sparkblog.model.Comment;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SimpleBlogEntryDAO implements BlogEntryDAO{

    //In memory Database for blog entries.
    private List<BlogEntry> blogEntries;
    private Slugify slg = new Slugify();


    public SimpleBlogEntryDAO() {
        Blog blog = new Blog();
        blogEntries = blog.getBlogEntries();
    }




    @Override
    public void addBlogEntry(BlogEntry blogEntry) {
        blogEntries.add(blogEntry);
    }

    public void setBlogEntries(List<BlogEntry> blogEntries) {
        this.blogEntries = blogEntries;
    }

    public void addBlogEntry(String title, String body) throws BlogNotValidException {
        if(Objects.equals(title, "") | Objects.equals(body, "")) {
            throw new BlogNotValidException();
        }
        String slug = slg.slugify(title);
        BlogEntry newBlogEntry = new BlogEntry(title,new Date(),body,slug);
        blogEntries.add(newBlogEntry);
    }

     public BlogEntry getBlogEntryForSlug(String blogSlug) throws BlogNotFoundException {
        return blogEntries.stream()
                .filter(currentBlogEntry -> currentBlogEntry.getSlug().equals(blogSlug))
                .findFirst()
                .orElseThrow(() -> new BlogNotFoundException());
    }

    public void addComment(String blogSlug,String commentName, String commentBody) throws BlogNotFoundException, CommentNotValidException {
        //Get the blog entry with the provided Slug
        BlogEntry blogEntryWithRequiredTitle = getBlogEntryForSlug(blogSlug);

        //If the user entered comment is blank
        if(commentName.equals("") || commentBody.equals("")) {
            throw new CommentNotValidException();
        }

        //Add comment
        blogEntryWithRequiredTitle.getComments().add(new Comment(commentName,commentBody));
    }

    @Override
    public List<BlogEntry> getAllBlogs() {
        return blogEntries;
    }



    @Override
    public List<Comment> getAllComments(String blogSlug) throws BlogNotFoundException {
       return getBlogEntryForSlug(blogSlug).getComments();
    }

    //Slug need to be changed whenever the body is changed...
    public void editBlogEntry(BlogEntry blogEntry , String newTitle , String newBody){
        blogEntry.setTitle(newTitle);
        blogEntry.setSlug(slg.slugify(newTitle));
        blogEntry.setBody(newBody);
    }

}
