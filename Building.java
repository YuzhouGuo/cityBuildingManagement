package assignment3;

public class Building {

	OneBuilding data;
	Building older;
	Building same;
	Building younger;
	
	public Building(OneBuilding data){
		this.data = data;
		this.older = null;
		this.same = null;
		this.younger = null;
	}
	
	public String toString(){
		String result = this.data.toString() + "\n";
		if (this.older != null){
			result += "older than " + this.data.toString() + " :\n";
			result += this.older.toString();
		}
		if (this.same != null){
			result += "same age as " + this.data.toString() + " :\n";
			result += this.same.toString();
		}
		if (this.younger != null){
			result += "younger than " + this.data.toString() + " :\n";
			result += this.younger.toString();
		}
		return result;
	}
	
	public Building addBuilding (OneBuilding b){
		Building nb = new Building(b);

		if (b.yearOfConstruction == this.data.yearOfConstruction)
		{
			if (b.height > this.data.height) //change the root
			{
				Building temp = new Building(this.data);
				temp.younger = this.younger; temp.older = this.older; temp.same = this.same;
				this.older = this.younger = this.same = null;
				this.data = b; this.younger = nb.younger; this.older = nb.older; 
				this.same = temp; this.younger = temp.younger; this.older = temp.older;
				temp.younger = temp.older = null;
			}
			else if ((b.height < this.data.height) && (this.same == null))
			{
				this.same = nb;
			}
			else if ((b.height <= this.data.height) && (this.same != null))
			{
				if (this.same.data.height < b.height)
				{
					Building temp = new Building(this.same.data);
					temp.younger = this.same.younger; temp.older = this.same.older; temp.same = this.same.same;
					this.same.older = this.same.younger = this.same.same = null;
					nb.younger = temp.younger; nb.older = temp.older; 
					this.same = nb;
					nb.same = temp;
					temp.younger = temp.older = null;
				}
				else
					this.same = this.same.addBuilding(b); 
			}
		}
		else if (b.yearOfConstruction > this.data.yearOfConstruction)
		{
			if (this.younger == null)
				this.younger = nb;
			if (this.younger != null)
				this.younger = this.younger.addBuilding(b);
		}
		
		else if (b.yearOfConstruction < this.data.yearOfConstruction)
		{
			if (this.older == null)
				this.older = nb;
			if (this.older != null)
				this.older = this.older.addBuilding(b);
		}
		return this;
	}
	
	public Building addBuildings (Building b){		
		this.addBuilding(b.data);
		
		if (b.younger != null)
			this.addBuildings(b.younger);
			
		if (b.older != null)
			this.addBuildings(b.older);

		if (b.same != null)
			this.addBuildings(b.same);
		
		return this;
	}
	
	public Building removeBuilding (OneBuilding b){
		if (this.findBuilding(b) == null)
			return this;
		
		Building temp = new Building(this.data);
		temp.younger = this.younger; temp.older = this.older; temp.same = this.same;
	
		if (b.yearOfConstruction < temp.data.yearOfConstruction)
			temp.older = temp.older.removeBuilding(b);
		else if (b.yearOfConstruction > temp.data.yearOfConstruction)
			temp.younger = temp.younger.removeBuilding(b);
		else if ((b.yearOfConstruction == temp.data.yearOfConstruction) && (b.equals(temp.data) == false))
			temp.same = temp.same.removeBuilding(b);
		
		else if (b.equals(temp.data))
		{
			if (temp.same != null)
			{	
				if (temp.younger != null)
					temp.same = temp.same.addBuildings(temp.younger);
				if (temp.older != null)
					temp.same = temp.same.addBuildings(temp.older);
				 temp = temp.same;
			}
			else if ((temp.same == null) && (temp.older != null))
			{
				if (temp.younger != null)
					temp.older = temp.older.addBuildings(temp.younger);
				temp = temp.older;
			}
			else if ((temp.same == null) && (temp.older == null))
				temp = temp.younger;
			return temp;
		}
		return temp; 
	}
	
	public int oldest(){
		if (this.older == null)
			return this.data.yearOfConstruction;
		else
			return this.older.oldest();
	}
	
	public int highest(){
		int currentHeight = this.data.height;
		int youngerHeight;
		int olderHeight;
		
		if (this.older != null)
		{
			olderHeight = this.older.highest();
			if (olderHeight >= currentHeight)
				currentHeight = olderHeight;
		}
		if (this.younger != null)
		{
			youngerHeight = this.younger.highest();
			if (youngerHeight >= currentHeight)
				currentHeight = youngerHeight;
		}
		return currentHeight;
	}
	
	public OneBuilding highestFromYear (int year){
		if ((year > this.data.yearOfConstruction) && (this.younger != null))
			return this.younger.highestFromYear(year);
		else if ((year < this.data.yearOfConstruction) && (this.older != null))
			return this.older.highestFromYear(year);
		else if (year == this.data.yearOfConstruction)
			return this.data;
		return null;
	}
	
	public int numberFromYears (int yearMin, int yearMax){
		if (yearMin > yearMax)
			return 0;
		
		int year = 0;
		for (int i=yearMin; i<=yearMax; i++)
			year += this.numberFromYear(i);
		return year;
	}
	
	public int[] costPlanning (int nbYears){
		int[] cost = new int[nbYears];
		
		if ((this.data.yearForRepair<=(2018+nbYears-1)) && (this.data.yearForRepair>=2018))
			cost[this.data.yearForRepair-2018] += this.data.costForRepair;
			
		if (this.older != null)
		{
			for (int i=0; i<nbYears; i++)
				cost[i] += this.older.costPlanning(nbYears)[i];
		}
		if (this.same != null)
		{
			for (int i=0; i<nbYears; i++)
				cost[i] += this.same.costPlanning(nbYears)[i];
		}
		if (this.younger != null)
		{
			for (int i=0; i<nbYears; i++)
				cost[i] += this.younger.costPlanning(nbYears)[i];
		}	
		return cost;
	}
	
	public Building findBuilding (OneBuilding b)
	{		
		if (b.yearOfConstruction == this.data.yearOfConstruction)
		{
			if (b.equals(this.data))
				return this;
			else if (this.same != null)
				return this.same.findBuilding(b);
			
			else if (this.same == null)
				return null;
		}	
		else if (b.yearOfConstruction > this.data.yearOfConstruction)
		{
			if (this.younger != null)
				return this.younger.findBuilding(b);
			else
				return null;
		}
		else if (b.yearOfConstruction < this.data.yearOfConstruction)
		{
			if (this.older != null)
				return this.older.findBuilding(b);
			else 
				return null;
		}
		return this;
	}
	
	public int numberFromYear (int year){
		int counter  = 0;
		
		if (this.younger != null)
			counter += this.younger.numberFromYear(year);
		
		if (this.older != null)
			counter += this.older.numberFromYear(year);
		
		if (this.same != null)
		    counter += this.same.numberFromYear(year);
		
		if (year == this.data.yearOfConstruction)
			counter += 1;
		
		return counter;
	}
}
