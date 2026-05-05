package ConditionalSuccessions {

	private import ScalarValues::Boolean;
	
	action def Focus;
	action def Shoot;
	
	// 1. Define the attribute usage directly inside the item def
	item def Image {
		attribute isFocused : Boolean;
	}

	action def TakePicture {
		first start;
		
		
		in scene;
		action focus : Focus {
			in scene = takePicture::scene;
			// 2. Type the output parameter so the compiler knows it has an 'isFocused' attribute
			out image : Image; 
		}
		
		first focus
			if focus.image.isFocused then shoot;

		flow focus.image to shoot.image;
		
		action shoot : Shoot {
			// 3. Type the input parameter to match
			in image : Image; 
			out picture;
		}

		out picture = shoot.picture;
	}
	action takePicture : TakePicture;
}