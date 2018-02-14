using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UnityBroadcastReciever : MonoBehaviour {

	[SerializeField] Text textBox;
	// Use this for initialization
	void Start () {
		
	}
	
	// Update is called once per frame
	void Update () {
		
	}

	void changeText(string newText){
		textBox.text = newText;
	}
}
