//******************************************************************************
// Copyright (C) 2020 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Wed Feb 12 23:13:57 2020 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20200212 [weaver]:	Original file.
//
//******************************************************************************
//
//******************************************************************************

package edu.ou.cs.hci.assignment.prototypeb;

//import java.lang.*;
import java.util.List;
import javafx.beans.property.*;

//******************************************************************************

/**
 * The <CODE>Movie</CODE> class manages the attributes of a movie as a set of
 * properties. The properties are created in the constructor. This differs from
 * the lazy creation of properties described in the TableView API (in the Person
 * class example), because we also use the properties to store the results of
 * parsing the inputs when the application starts.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */

public final class Movie
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// Each attribute has a matching property of the corresponding type.

	private final SimpleStringProperty		title; 
	private final SimpleStringProperty		image;	
	private final SimpleStringProperty		comments; 
	private final SimpleStringProperty		director; 
	private final SimpleStringProperty		rating;
	private final SimpleStringProperty		summary; 


	private final SimpleDoubleProperty		reviewScore; 
	
	private final SimpleBooleanProperty		pictureAward; 
	private final SimpleBooleanProperty		directingAward; 
	private final SimpleBooleanProperty		cinemAward; 
	private final SimpleBooleanProperty		actingAward; 
	private final SimpleBooleanProperty		isAnimated; 
	private final SimpleBooleanProperty		isColor; 
	
	private final SimpleIntegerProperty		numberOfReviews; 
	private final SimpleIntegerProperty		runtime; 
	private final SimpleIntegerProperty		year; 
	private final SimpleIntegerProperty		genre; 

	// TODO #0: Add members for the other 15 attributes.

	//private final SimpleFootypeProperty	foo;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Movie(List<String> item)
	{
		// Each attribute value must be calculated from its string.

		title = new SimpleStringProperty(item.get(0));
		image = new SimpleStringProperty(item.get(1));
		year = new SimpleIntegerProperty((Integer.valueOf(item.get(2))));
		rating = new SimpleStringProperty(item.get(3));
		runtime = new SimpleIntegerProperty((Integer.parseInt(item.get(4))));
		pictureAward = new SimpleBooleanProperty((Boolean.valueOf(item.get(5))));
		directingAward = new SimpleBooleanProperty((Boolean.valueOf(item.get(6))));
		cinemAward = new SimpleBooleanProperty((Boolean.valueOf(item.get(7))));
		actingAward = new SimpleBooleanProperty((Boolean.valueOf(item.get(8))));
		reviewScore = new SimpleDoubleProperty((Double.valueOf(item.get(9))));
		numberOfReviews = new SimpleIntegerProperty(Integer.parseInt(item.get(10)));
		genre = new SimpleIntegerProperty((Integer.valueOf(item.get(11))));
		director = new SimpleStringProperty(item.get(12));
		isAnimated = new SimpleBooleanProperty((Boolean.valueOf(item.get(13))));
		isColor = new SimpleBooleanProperty((Boolean.valueOf(item.get(14))));
		summary = new SimpleStringProperty(item.get(15));
		comments = new SimpleStringProperty(item.get(16));


		// TODO #1: Create properties for the other attributes. For non-string
		// types, look for methods in the Boolean, Integer, and Double classes.

		//foo = new SimpleFootypeProperty(item.get(2));

		// Hint for genres: An integer can be treated as a collection of
		// independently set bits. See genre code in EditorPane for examples.
	}

	//**********************************************************************
	// Public Methods (Getters and Setters)
	//**********************************************************************

	// Each attribute has methods to access and modify its value.

	public String	getTitle()
	{
		return title.get();
	}

	public void	setTitle(String v)
	{
		title.set(v);
	}

	public String	getImage()
	{
		return image.get();
	}

	public void	setImage(String v)
	{
		image.set(v);
	}
	
	public String	getComments()
	{
		return comments.get();
	}

	public void	setComments(String v)
	{
		comments.set(v);
	}
	public String	getDirector()
	{
		return director.get();
	}

	public void	setDirector(String v)
	{
		director.set(v);
	}
	public String getRating()
	{
		return rating.get();
	}

	public void	setRating(String v)
	{
		rating.set(v);
	}
	public String	getSummary()
	{
		return summary.get();
	}

	public void	setSummary(String v)
	{
		summary.set(v);
	}
	public Double getReviewScore()
	{
		return reviewScore.get();
	}

	public void	setReviewScore(Double v)
	{
		reviewScore.set(v);
	}
	public Boolean getPictureAward()
	{
		return pictureAward.get();
	}

	public void	setPictureAward(Boolean v)
	{
		pictureAward.set(v);
	}
	public Boolean getDirectingAward()
	{
		return directingAward.get();
	}

	public void	setDirectingAward(Boolean v)
	{
		directingAward.set(v);
	}
	public Boolean getCinemaAward()
	{
		return cinemAward.get();
	}

	public void	setCinemaAward(Boolean v)
	{
		cinemAward.set(v);
	}
	public Boolean getActingAward()
	{
		return actingAward.get();
	}

	public void	setActingAward(Boolean v)
	{
		actingAward.set(v);
	}
	public Boolean getIsAnimated()
	{
		return isAnimated.get();
	}

	public void	setIsAnimated(Boolean v)
	{
		isAnimated.set(v);
	}
	
	public Boolean getIsColor()
	{
		return isColor.get();
	}

	public void	setIsColor(Boolean v)
	{
		isColor.set(v);
	}
	public Integer getNumberOfReviews()
	{
		return numberOfReviews.get();
	}

	public void	setNumberOfReviews(Integer v)
	{
		numberOfReviews.set(v);
	}
	public Integer getRuntime()
	{
		return runtime.get();
	}

	public void	setRuntime(Integer v)
	{
		runtime.set(v);
	}
	public Integer getYear()
	{
		return year.get();
	}

	public void	setYear(Integer v)
	{
		year.set(v);
	}
	public Integer getGenre()
	{
		return genre.get();
	}

	public void	setGenre(Integer v)
	{
		genre.set(v);
	}
	

	// TODO #2: Create access and modify methods for your three attributes.

	//public Footype	getFoo()
	//{
	//	return foo.get();
	//}

	//public void	setFoo(Footype v)
	//{
	//	foo.set(v);
	//}
}
//******************************************************************************
